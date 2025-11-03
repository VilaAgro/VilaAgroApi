package com.vilaagro.api.dto;

import com.vilaagro.api.model.AccountStatus;
import com.vilaagro.api.model.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO para criação de novos usuários
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDTO {

    private UUID salePointId;

    @NotBlank(message = "Nome é obrigatório")
    private String name;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter formato válido")
    private String email;

    private String cpf;

    @NotBlank(message = "Senha é obrigatória")
    private String password;

    private AccountStatus documentsStatus;

    @NotNull(message = "Tipo de usuário é obrigatório")
    private UserType type;
}
