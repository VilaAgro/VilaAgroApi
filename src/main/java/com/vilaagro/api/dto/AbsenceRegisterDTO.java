package com.vilaagro.api.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class AbsenceRegisterDTO {

    @NotNull(message = "Data é obrigatória")
    private LocalDate date;

    @NotEmpty(message = "Lista de usuários é obrigatória")
    private List<UUID> userIds;
}