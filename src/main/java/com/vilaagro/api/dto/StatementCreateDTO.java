// VilaAgroApi/src/main/java/com/vilaagro/api/dto/StatementCreateDTO.java
package com.vilaagro.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StatementCreateDTO {

    @NotBlank(message = "A mensagem é obrigatória")
    private String message;

    private String stereotype;
}