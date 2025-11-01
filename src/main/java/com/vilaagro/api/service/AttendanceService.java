package com.vilaagro.api.service;

import com.vilaagro.api.dto.*;
import com.vilaagro.api.exception.ResourceNotFoundException;
import com.vilaagro.api.model.*;
import com.vilaagro.api.repository.AbsenceRepository;
import com.vilaagro.api.repository.JustificationRepository;
import com.vilaagro.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AttendanceService {

    private final AbsenceRepository absenceRepository;
    private final JustificationRepository justificationRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public List<AbsenceResponseDTO> registerAbsences(AbsenceRegisterDTO registerDTO) {
        List<Absence> createdAbsences = new ArrayList<>();

        for (UUID userId : registerDTO.getUserIds()) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", userId));

            if (user.getDocumentsStatus() != AccountStatus.ACTIVE) {
                log.warn("Tentativa de registrar falta para usuário não-ativo: {}", userId);
                continue;
            }

            if (absenceRepository.findByUserIdAndDate(userId, registerDTO.getDate()).isEmpty()) {
                Absence absence = Absence.builder()
                        .user(user)
                        .date(registerDTO.getDate())
                        .isAccepted(false)
                        .build();
                createdAbsences.add(absenceRepository.save(absence));
            }
        }
        return createdAbsences.stream()
                .map(AbsenceResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Admin/User: Busca todas as ausências de um usuário específico (RF-C.3.1)
     */
    @Transactional(readOnly = true)
    public List<AbsenceResponseDTO> getAbsencesByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Usuário", "id", userId);
        }
        return absenceRepository.findByUserIdOrderByDateDesc(userId)
                .stream()
                .map(AbsenceResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public JustificationResponseDTO submitJustification(UUID absenceId, JustificationCreateDTO createDTO, User currentUser) {
        Absence absence = absenceRepository.findById(absenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Ausência", "id", absenceId));

        if (!absence.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Você não tem permissão para justificar esta ausência.");
        }

        if (justificationRepository.existsByAbsenceId(absenceId)) {
            throw new IllegalStateException("Esta ausência já possui uma justificativa.");
        }

        JustificationForAbsence justification = JustificationForAbsence.builder()
                .absence(absence)
                .description(createDTO.getDescription())
                .isApproved(null) // Pendente de análise
                .build();

        JustificationForAbsence saved = justificationRepository.save(justification);

        absence.setJustification(saved);
        AbsenceResponseDTO dto = AbsenceResponseDTO.fromEntity(absence);
        return dto.getJustification();
    }
    // Admin
    @Transactional(readOnly = true)
    public List<AbsenceResponseDTO> getPendingJustifications() {
        return justificationRepository.findPendingJustifications()
                .stream()
                .map(justification -> AbsenceResponseDTO.fromEntity(justification.getAbsence()))
                .collect(Collectors.toList());
    }

    public AbsenceResponseDTO reviewJustification(UUID justificationId, JustificationReviewDTO reviewDTO, User adminUser) {
        JustificationForAbsence justification = justificationRepository.findById(justificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Justificativa", "id", justificationId));

        Absence absence = justification.getAbsence();

        justification.setIsApproved(reviewDTO.getIsApproved());
        justification.setApprovedByAdminId(adminUser.getId());

        if (reviewDTO.getIsApproved()) {
            absence.setIsAccepted(true);
            absenceRepository.save(absence);
        } else {
            absence.setIsAccepted(false);
            absenceRepository.save(absence);
        }

        justificationRepository.save(justification);

        return AbsenceResponseDTO.fromEntity(absence);
    }
}