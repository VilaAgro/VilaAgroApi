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

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
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
    private final FileStorageService fileStorageService;

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
                        .type(AbsenceType.REGISTERED)
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

    public JustificationResponseDTO submitJustification(
            UUID absenceId, 
            JustificationCreateDTO createDTO, 
            MultipartFile file,
            User currentUser
    ) {
        Absence absence = absenceRepository.findById(absenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Ausência", "id", absenceId));

        if (!absence.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Você não tem permissão para justificar esta ausência.");
        }

        // Verifica se já existe justificativa
        JustificationForAbsence justification = absence.getJustification();
        
        // Processa upload do arquivo (se fornecido)
        String filePath = null;
        String originalFileName = null;
        
        if (file != null && !file.isEmpty()) {
            filePath = fileStorageService.storeFile(file, "justifications/" + currentUser.getId());
            originalFileName = file.getOriginalFilename();
            log.info("Arquivo anexado à justificação: {}", originalFileName);
        }

        if (justification != null) {
            // Atualiza justificativa existente
            justification.setDescription(createDTO.getDescription());
            if (filePath != null) {
                justification.setFilePath(filePath);
                justification.setOriginalFileName(originalFileName);
            }
            log.info("Justificativa atualizada para ausência: {}", absenceId);
        } else {
            // Cria nova justificativa
            justification = JustificationForAbsence.builder()
                    .absence(absence)
                    .description(createDTO.getDescription())
                    .filePath(filePath)
                    .originalFileName(originalFileName)
                    .isApproved(null) // Pendente de análise
                    .build();
            log.info("Nova justificativa criada para ausência: {}", absenceId);
        }

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

    /**
     * Retorna resumo de frequência do usuário (últimos 12 meses)
     */
    @Transactional(readOnly = true)
    public AttendanceSummaryDTO getAttendanceSummary(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Usuário", "id", userId);
        }

        // Data de 12 meses atrás
        LocalDate twelveMonthsAgo = LocalDate.now().minusMonths(12);

        // Busca todas as ausências dos últimos 12 meses
        List<Absence> absences = absenceRepository.findByUserIdOrderByDateDesc(userId)
                .stream()
                .filter(a -> a.getDate().isAfter(twelveMonthsAgo) || a.getDate().isEqual(twelveMonthsAgo))
                .collect(Collectors.toList());

        int totalAbsences = absences.size();
        int justifiedAbsences = (int) absences.stream()
                .filter(Absence::getIsAccepted)
                .count();
        int unjustifiedAbsences = totalAbsences - justifiedAbsences;

        // Calcula faltas consecutivas
        int consecutiveAbsences = calculateConsecutiveAbsences(absences);

        // Consideramos "em conformidade" se:
        // - Tem menos de 3 faltas injustificadas OU
        // - Todas as faltas foram justificadas
        boolean isCompliant = unjustifiedAbsences < 3;

        return AttendanceSummaryDTO.builder()
                .totalAbsences(totalAbsences)
                .justifiedAbsences(justifiedAbsences)
                .unjustifiedAbsences(unjustifiedAbsences)
                .consecutiveAbsences(consecutiveAbsences)
                .isCompliant(isCompliant)
                .build();
    }

    /**
     * Calcula o número máximo de faltas consecutivas
     */
    private int calculateConsecutiveAbsences(List<Absence> absences) {
        if (absences.isEmpty()) {
            return 0;
        }

        // Ordena por data (mais recente primeiro)
        List<LocalDate> dates = absences.stream()
                .map(Absence::getDate)
                .sorted((d1, d2) -> d2.compareTo(d1))
                .collect(Collectors.toList());

        int maxConsecutive = 1;
        int currentConsecutive = 1;

        for (int i = 1; i < dates.size(); i++) {
            LocalDate current = dates.get(i);
            LocalDate previous = dates.get(i - 1);

            // Verifica se as datas são consecutivas (considerando apenas dias úteis/feiras)
            // Assumindo que feiras são semanais (7 dias de diferença)
            if (previous.minusDays(7).equals(current)) {
                currentConsecutive++;
                maxConsecutive = Math.max(maxConsecutive, currentConsecutive);
            } else {
                currentConsecutive = 1;
            }
        }

        return maxConsecutive;
    }

    /**
     * Comerciante notifica ausência futura
     */
    public AbsenceResponseDTO notifyAbsence(UUID userId, AbsenceNotificationDTO notificationDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", userId));

        // Verifica se já existe ausência registrada para esta data
        if (absenceRepository.findByUserIdAndDate(userId, notificationDTO.getAbsenceDate()).isPresent()) {
            throw new IllegalStateException("Já existe uma ausência registrada para esta data");
        }

        // Cria a ausência notificada (avisada com antecedência)
        Absence absence = Absence.builder()
                .user(user)
                .date(notificationDTO.getAbsenceDate())
                .type(AbsenceType.NOTIFIED)
                .isAccepted(false) // Pendente de aprovação do admin
                .build();

        Absence savedAbsence = absenceRepository.save(absence);

        // Se há motivo, cria a justificativa automaticamente
        if (notificationDTO.getReason() != null && !notificationDTO.getReason().trim().isEmpty()) {
            JustificationForAbsence justification = JustificationForAbsence.builder()
                    .absence(savedAbsence)
                    .description(notificationDTO.getReason())
                    .isApproved(null) // Pendente de análise
                    .build();

            JustificationForAbsence savedJustification = justificationRepository.save(justification);
            savedAbsence.setJustification(savedJustification);
        }

        log.info("Ausência notificada: usuário={}, data={}", userId, notificationDTO.getAbsenceDate());
        
        // TODO: Enviar notificação para admin sobre a ausência notificada
        
        return AbsenceResponseDTO.fromEntity(savedAbsence);
    }
}