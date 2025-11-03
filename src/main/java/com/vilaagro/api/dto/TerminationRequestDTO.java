package com.vilaagro.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitação de desligamento do usuário
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TerminationRequestDTO {

    @NotBlank(message = "Motivo do desligamento é obrigatório")
    private String reason;

    private String details;
}
