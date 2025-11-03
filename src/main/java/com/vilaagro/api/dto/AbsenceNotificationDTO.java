package com.vilaagro.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para notificação de ausência do comerciante
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AbsenceNotificationDTO {
    
    @NotNull(message = "Data da ausência é obrigatória")
    private LocalDate absenceDate;
    
    private String reason;
}
