package com.vilaagro.api.dto;

import com.vilaagro.api.model.Absence;
import com.vilaagro.api.model.AbsenceType;
import com.vilaagro.api.model.JustificationForAbsence;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AbsenceResponseDTO {
    private UUID id;
    private UUID userId;
    private LocalDate date;
    private AbsenceType type;
    private Boolean isAccepted;
    private JustificationResponseDTO justification;
    private LocalDateTime createdAt;

    public static AbsenceResponseDTO fromEntity(Absence absence) {
        JustificationResponseDTO justifDTO = null;
        JustificationForAbsence justification = absence.getJustification();

        if (justification != null) {
            justifDTO = JustificationResponseDTO.builder()
                    .id(justification.getId())
                    .absenceId(absence.getId())
                    .description(justification.getDescription())
                    .isApproved(justification.getIsApproved())
                    .approvedByAdminId(justification.getApprovedByAdminId())
                    .createdAt(justification.getCreatedAt())
                    .build();
        }

        return AbsenceResponseDTO.builder()
                .id(absence.getId())
                .userId(absence.getUser().getId())
                .date(absence.getDate())
                .type(absence.getType())
                .isAccepted(absence.getIsAccepted())
                .justification(justifDTO)
                .createdAt(absence.getCreatedAt())
                .build();
    }
}