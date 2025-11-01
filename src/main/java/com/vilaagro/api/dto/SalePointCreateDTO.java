package com.vilaagro.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.UUID;

@Data
public class SalePointCreateDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String name;
    private UUID addressId;
}