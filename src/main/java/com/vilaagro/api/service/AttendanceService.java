package com.vilaagro.api.service;

import com.vilaagro.api.dto.*;
import com.vilaagro.api.exception.ResourceNotFoundException;
import com.vilaagro.api.model.*;
import com.vilaagro.api.model.UserType; // Import necessário
import com.vilaagro.api.repository.AbsenceRepository;
import com.vilaagro.api.repository.JustificationRepository;
import com.vilaagro.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile; // Import necessário
import java.io.IOException; // Import necessário

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
     * (Agora com suporte a anexo RF-C.3.3)
     */
    public JustificationResponseDTO submitJustification(
            UUID absenceId,
            String description,
            MultipartFile file,
            User currentUser
    ) {
        Absence absence = absenceRepository.findById(absenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Ausência", "id", absenceId));

        if (!absence.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Você não tem permissão para justificar esta ausência.");
        }
        if (justificationRepository.existsByAbsenceId(absenceId)) {
            throw new IllegalStateException("Esta ausência já possui uma justificativa.");
        }

        byte[] annexBytes = null;
        if (file != null && !file.isEmpty()) {
            try {
                annexBytes = file.getBytes();
            } catch (IOException e) {
                log.error("Erro ao ler bytes do anexo da justificativa", e);
                throw new RuntimeException("Erro ao processar anexo: " + e.getMessage());
            }
        }

        JustificationForAbsence justification = JustificationForAbsence.builder()
                .absence(absence)
                .description(description)
                .annex(annexBytes)
                .isApproved(null) // Pendente de análise
                .build();

        JustificationForAbsence saved = justificationRepository.save(justification);

        absence.setJustification(saved);
        AbsenceResponseDTO dto = AbsenceResponseDTO.fromEntity(absence);
        return dto.getJustification();
    }

    /**
     * Admin: Busca todas as justificativas pendentes de análise (RF-D.1.5, RF-D.6.2)
     *
     * ESTE É O MÉTODO QUE FALTAVA
     */
    @Transactional(readOnly = true)
    public List<AbsenceResponseDTO> getPendingJustifications() {
        return justificationRepository.findPendingJustifications()
                .stream()
                .map(justification -> AbsenceResponseDTO.fromEntity(justification.getAbsence()))
                .collect(Collectors.toList());
    }

    /**
     * Admin: Aprova ou Reprova uma justificativa (RF-D.6.2)
     */
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

    /**
     * Admin/User: Busca os bytes do anexo de uma justificativa
     */
    @Transactional(readOnly = true)
    public byte[] getJustificationAnnex(UUID justificationId, User currentUser) {
        JustificationForAbsence justification = justificationRepository.findById(justificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Justificativa2", "id", justificationId));

        boolean isOwner = justification.getAbsence().getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getType() == UserType.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("Você não tem permissão para acessar este anexo.");
        }

        if (justification.getAnnex() == null || justification.getAnnex().length == 0) {
            throw new ResourceNotFoundException("Justificativa", "id", justificationId + " (Anexo não encontrado)");
        }

        return justification.getAnnex();
    }

    public AbsenceResponseDTO notifyAbsence(UUID userId, AbsenceNotificationDTO notificationDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", userId));

        // 1. Verifica se já existe uma falta registrada para este dia
        if (absenceRepository.findByUserIdAndDate(userId, notificationDTO.getDate()).isPresent()) {
            throw new IllegalStateException("Já existe uma falta registrada ou notificada para esta data.");
        }

        // 2. Cria a Ausência (Falta)
        Absence absence = Absence.builder()
                .user(user)
                .date(notificationDTO.getDate())
                .isAccepted(false) // Admin ainda não aceitou (RN-D.6.1)
                .build();
        Absence savedAbsence = absenceRepository.save(absence);

        // 3. Cria a Justificativa (baseado no motivo)
        JustificationForAbsence justification = JustificationForAbsence.builder()
                .absence(savedAbsence)
                .description(notificationDTO.getReason())
                .isApproved(null) // PENDENTE de análise do Admin (RN-C.3.3)
                .build();
        JustificationForAbsence savedJustification = justificationRepository.save(justification);

        // 4. Associa a justificativa à ausência e retorna o DTO
        savedAbsence.setJustification(savedJustification);
        return AbsenceResponseDTO.fromEntity(savedAbsence);
    }

    /**
     * Comerciante: Obtém um resumo da sua frequência
     * (Novo método do seu AttendanceController)
     * (RF-C.1.3)
     */
    @Transactional(readOnly = true)
    public AttendanceSummaryDTO getAttendanceSummary(UUID userId) {
        List<Absence> absences = absenceRepository.findByUserIdOrderByDateDesc(userId);

        long total = absences.size();

        long justified = absences.stream()
                .filter(a -> Boolean.TRUE.equals(a.getIsAccepted()))
                .count();

        long pending = absences.stream()
                .filter(a -> a.getJustification() != null && a.getJustification().getIsApproved() == null)
                .count();

        long unjustified = total - justified - pending;

        return AttendanceSummaryDTO.builder()
                .totalAbsences(total)
                .justifiedAbsences(justified)
                .pendingJustifications(pending)
                .unjustifiedAbsences(unjustified)
                .build();
    }
}