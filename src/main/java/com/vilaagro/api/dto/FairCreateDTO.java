package com.vilaagro.api.dto;

import com.vilaagro.api.model.FairStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FairCreateDTO {

    @NotNull(message = "A data é obrigatória")
    @FutureOrPresent(message = "A data da feira deve ser no presente ou futuro")
    private LocalDate date;

    @NotBlank(message = "A hora de início é obrigatória")
    @Pattern(regexp = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Hora de início deve estar no formato HH:mm")
    private String startTime;

    @NotBlank(message = "A hora de término é obrigatória")
    @Pattern(regexp = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Hora de término deve estar no formato HH:mm")
    private String endTime;

    private String notes;

    @NotNull(message = "O status é obrigatório")
    private FairStatus status;
}