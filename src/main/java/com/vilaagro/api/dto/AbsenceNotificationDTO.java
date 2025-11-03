package com.vilaagro.api.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

/**
 * DTO para o comerciante notificar uma ausência futura (RF-C.5)
 */
@Data
public class AbsenceNotificationDTO {

    @NotNull(message = "A data é obrigatória")
    @FutureOrPresent(message = "A data deve ser hoje ou no futuro")
    private LocalDate date;

    @NotBlank(message = "O motivo é obrigatório")
    private String reason;
}