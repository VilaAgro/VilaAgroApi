package com.vilaagro.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JustificationCreateDTO {

    @NotBlank(message = "Descrição é obrigatória")
    private String description;

    // O anexo (arquivo) seria tratado aqui, geralmente como MultipartFile
}