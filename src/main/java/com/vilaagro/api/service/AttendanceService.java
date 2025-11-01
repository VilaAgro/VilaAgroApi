// VilaAgroApi/src/main/java/com/vilaagro/api/service/AttendanceService.java
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

    /**
     * Admin: Registra faltas para múltiplos usuários em uma data (RF-D.6.1)
     */
    public List<AbsenceResponseDTO> registerAbsences(AbsenceRegisterDTO registerDTO) {
        List<Absence> createdAbsences = new ArrayList<>();

        for (UUID userId : registerDTO.getUserIds()) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", userId));

            // RN: Só registra falta para usuários ATIVOS (RF-D.6.1)
            if (user.getDocumentsStatus() != AccountStatus.ACTIVE) {
                log.warn("Tentativa de registrar falta para usuário não-ativo: {}", userId);
                continue;
            }

            // Evita duplicatas
            if (absenceRepository.findByUserIdAndDate(userId, registerDTO.getDate()).isEmpty()) {
                Absence absence = Absence.builder()
                        .user(user)
                        .date(registerDTO.getDate())
                        .isAccepted(false) // Falta não justificada
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

    /**
     * User: Envia uma justificativa para uma ausência (RF-C.3.2)
     */
    public JustificationResponseDTO submitJustification(UUID absenceId, JustificationCreateDTO createDTO, User currentUser) {
        Absence absence = absenceRepository.findById(absenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Ausência", "id", absenceId));

        // Validação de segurança: O usuário só pode justificar suas próprias faltas
        if (!absence.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Você não tem permissão para justificar esta ausência.");
        }

        // Validação RN: Não permite justificar duas vezes (RN-C.3.1)
        if (justificationRepository.existsByAbsenceId(absenceId)) {
            throw new IllegalStateException("Esta ausência já possui uma justificativa.");
        }

        // RN-C.3.2: Pode haver prazo. (Não implementado, mas aqui seria o local)

        JustificationForAbsence justification = JustificationForAbsence.builder()
                .absence(absence)
                .description(createDTO.getDescription())
                .isApproved(null) // Pendente de análise
                .build();

        // (Aqui viria a lógica de salvar o 'anexo' (arquivo))

        JustificationForAbsence saved = justificationRepository.save(justification);

        // Atualiza a DTO da ausência para incluir a justificativa recém-criada
        absence.setJustification(saved);
        AbsenceResponseDTO dto = AbsenceResponseDTO.fromEntity(absence);
        return dto.getJustification();
    }

    /**
     * Admin: Busca todas as justificativas pendentes de análise (RF-D.1.5, RF-D.6.2)
     */
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

        // (RN-D.6.2: Se reprovado, salvar o 'reason' em algum lugar. O schema não tem esse campo,
        // poderíamos salvar na 'description' da própria justificativa ou criar um novo campo)

        // RN-D.6.1: Se aprovado, marca a falta como "Justificada" (aceita)
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