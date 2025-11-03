// VilaAgroApi/src/main/java/com/vilaagro/api/controller/AttendanceController.java
package com.vilaagro.api.controller;

import com.vilaagro.api.dto.AbsenceNotificationDTO;
import com.vilaagro.api.dto.AbsenceRegisterDTO;
import com.vilaagro.api.dto.AbsenceResponseDTO;
import com.vilaagro.api.dto.AttendanceSummaryDTO;
import com.vilaagro.api.dto.JustificationCreateDTO;
import com.vilaagro.api.dto.JustificationResponseDTO;
import com.vilaagro.api.dto.JustificationReviewDTO;
import com.vilaagro.api.model.User;
import com.vilaagro.api.service.AttendanceService;
import com.vilaagro.api.service.CustomUserDetailsService;
import com.vilaagro.api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST para Frequência (Ausências) e Justificativas
 * (RF-C.3 e RF-D.6)
 */
@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final UserService userService;

    /**
     * Admin: Registra faltas para usuários em uma data (RF-D.6.1)
     */
    @PostMapping("/absences")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<AbsenceResponseDTO>> registerAbsences(
            @Valid @RequestBody AbsenceRegisterDTO registerDTO
    ) {
        List<AbsenceResponseDTO> absences = attendanceService.registerAbsences(registerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(absences);
    }

    /**
     * Admin: Lista justificativas pendentes (RF-D.6.2)
     */
    @GetMapping("/justifications/pending")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<AbsenceResponseDTO>> getPendingJustifications() {
        List<AbsenceResponseDTO> pending = attendanceService.getPendingJustifications();
        return ResponseEntity.ok(pending);
    }

    /**
     * Admin: Revisa (aprova/reprova) uma justificativa (RF-D.6.2)
     */
    @PutMapping("/justifications/{id}/review")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AbsenceResponseDTO> reviewJustification(
            @PathVariable UUID id,
            @Valid @RequestBody JustificationReviewDTO reviewDTO,
            @AuthenticationPrincipal CustomUserDetailsService.CustomUserPrincipal adminPrincipal
    ) {
        AbsenceResponseDTO reviewed = attendanceService.reviewJustification(id, reviewDTO, adminPrincipal.getUser());
        return ResponseEntity.ok(reviewed);
    }

    /**
     * Comerciante: Visualiza o próprio histórico de frequência (RF-C.3.1)
     */
    @GetMapping("/absences/me")
    public ResponseEntity<List<AbsenceResponseDTO>> getMyAbsences(
            @AuthenticationPrincipal CustomUserDetailsService.CustomUserPrincipal currentUser
    ) {
        List<AbsenceResponseDTO> absences = attendanceService.getAbsencesByUserId(currentUser.getUser().getId());
        return ResponseEntity.ok(absences);
    }

    /**
     * Comerciante: Envia uma justificativa para uma falta (RF-C.3.2)
     * Aceita multipart/form-data com arquivo opcional
     */
    @PostMapping(
            path = "/absences/{id}/justify",
            consumes = { MediaType.MULTIPART_FORM_DATA_VALUE } // <-- INFORMA QUE ACEITA ARQUIVOS
    )
    public ResponseEntity<?> submitJustification(
            @PathVariable UUID id,
            @RequestParam("description") String description, // <-- DTO substituído por RequestParam
            @RequestParam(value = "file", required = false) MultipartFile file, // <-- O ANEXO
            @AuthenticationPrincipal CustomUserDetailsService.CustomUserPrincipal currentUser
    ) {
        var justification = attendanceService.submitJustification(id, description, file, currentUser.getUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(justification);
    }

    /**
     * Admin/Comerciante: Baixa o anexo de uma justificativa
     */
    @GetMapping("/justifications/{id}/annex")
    public ResponseEntity<byte[]> getJustificationAnnex(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetailsService.CustomUserPrincipal currentUser
    ) {
        byte[] annexData = attendanceService.getJustificationAnnex(id, currentUser.getUser());

        // Retorna os bytes do arquivo para download
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"anexo.pdf\"") // Assume PDF, já que o DB não guarda o nome
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(annexData);
    }

    /**
     * Admin: Visualiza o histórico de frequência de um usuário específico
     */
    @GetMapping("/absences/user/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<AbsenceResponseDTO>> getAbsencesForUser(@PathVariable UUID userId) {
        List<AbsenceResponseDTO> absences = attendanceService.getAbsencesByUserId(userId);
        return ResponseEntity.ok(absences);
    }

    /**
     * Comerciante: Obtém resumo de frequência dos últimos 12 meses
     */
    @GetMapping("/summary")
    public ResponseEntity<AttendanceSummaryDTO> getAttendanceSummary(
            @AuthenticationPrincipal CustomUserDetailsService.CustomUserPrincipal currentUser
    ) {
        AttendanceSummaryDTO summary = attendanceService.getAttendanceSummary(currentUser.getUser().getId());
        return ResponseEntity.ok(summary);
    }

    /**
     * Comerciante: Notifica ausência futura com motivo
     */
    @PostMapping("/absence/notify")
    public ResponseEntity<AbsenceResponseDTO> notifyAbsence(
            @Valid @RequestBody AbsenceNotificationDTO notificationDTO,
            @AuthenticationPrincipal CustomUserDetailsService.CustomUserPrincipal currentUser
    ) {
        AbsenceResponseDTO absence = attendanceService.notifyAbsence(
                currentUser.getUser().getId(),
                notificationDTO
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(absence);
    }
}
