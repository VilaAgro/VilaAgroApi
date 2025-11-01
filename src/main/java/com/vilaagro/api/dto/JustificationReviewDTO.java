package com.vilaagro.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JustificationReviewDTO {

    @NotNull(message = "Status de aprovação é obrigatório")
    private Boolean isApproved;

    private String reason; // (RN-D.6.2 - Motivo da reprovação)
}