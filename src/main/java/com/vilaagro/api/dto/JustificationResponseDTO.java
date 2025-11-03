package com.vilaagro.api.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class JustificationResponseDTO {
    private UUID id;
    private UUID absenceId;
    private String description;
    private Boolean isApproved;
    private UUID approvedByAdminId;
    private LocalDateTime createdAt;
    private boolean hasAnnex;
}