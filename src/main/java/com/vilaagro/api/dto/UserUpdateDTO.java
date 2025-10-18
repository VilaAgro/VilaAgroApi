package com.vilaagro.api.dto;

import com.vilaagro.api.model.AccountStatus;
import com.vilaagro.api.model.UserType;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO para atualização de usuários
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {

    private UUID salePointId;
    private String name;

    @Email(message = "Email deve ter formato válido")
    private String email;

    private String password;
    private AccountStatus documentsStatus;
    private UserType type;
}
