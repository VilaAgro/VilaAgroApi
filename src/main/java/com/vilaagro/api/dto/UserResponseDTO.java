package com.vilaagro.api.dto;

import com.vilaagro.api.model.AccountStatus;
import com.vilaagro.api.model.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para resposta com dados do usuário (sem senha)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    private UUID id;
    private UUID salePointId;
    private String name;
    private String email;
    private AccountStatus documentsStatus;
    private UserType type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
