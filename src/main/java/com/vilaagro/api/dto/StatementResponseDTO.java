// VilaAgroApi/src/main/java/com/vilaagro/api/dto/StatementResponseDTO.java
package com.vilaagro.api.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class StatementResponseDTO {

    private UUID id;
    private String message;
    private String stereotype;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Inclui os dados do admin que postou (RF-D.5)
    private UserResponseDTO admin;
}