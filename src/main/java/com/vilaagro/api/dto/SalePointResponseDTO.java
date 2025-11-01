package com.vilaagro.api.dto;

import com.vilaagro.api.model.User;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class SalePointResponseDTO {
    private UUID id;
    private String name;
    private UUID addressId;
    private UserResponseDTO allocatedUser; // Resposta customizada para o usuário
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}