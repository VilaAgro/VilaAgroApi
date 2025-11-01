package com.vilaagro.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class SalePointAllocateDTO {

    @NotNull(message = "ID do usuário é obrigatório")
    private UUID userId;
}