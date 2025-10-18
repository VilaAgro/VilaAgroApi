package com.vilaagro.api.service;

import com.vilaagro.api.dto.UserCreateDTO;
import com.vilaagro.api.dto.UserResponseDTO;
import com.vilaagro.api.dto.UserUpdateDTO;
import com.vilaagro.api.exception.EmailAlreadyExistsException;
import com.vilaagro.api.exception.ResourceNotFoundException;
import com.vilaagro.api.model.User;
import com.vilaagro.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Serviço para gerenciamento de usuários
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Cria um novo usuário
     * Nota: A senha deve já estar hashada quando chegar aqui (feito no AuthService)
     */
    public UserResponseDTO createUser(UserCreateDTO createDTO) {
        // Verifica se o email já existe
        if (userRepository.existsByEmail(createDTO.getEmail())) {
            throw new EmailAlreadyExistsException(createDTO.getEmail());
        }

        User user = User.builder()
                .salePointId(createDTO.getSalePointId())
                .name(createDTO.getName())
                .email(createDTO.getEmail())
                .password(createDTO.getPassword()) // Senha já hashada pelo AuthService
                .documentsStatus(createDTO.getDocumentsStatus())
                .type(createDTO.getType())
                .build();

        User savedUser = userRepository.save(user);
        return convertToResponseDTO(savedUser);
    }

    /**
     * Lista todos os usuários
     */
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca um usuário por ID
     */
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", id));
        return convertToResponseDTO(user);
    }

    /**
     * Atualiza um usuário
     */
    public UserResponseDTO updateUser(UUID id, UserUpdateDTO updateDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", id));

        // Verifica se o email já existe (apenas se foi alterado)
        if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updateDTO.getEmail())) {
                throw new EmailAlreadyExistsException(updateDTO.getEmail());
            }
        }

        // Atualiza os campos não nulos
        if (updateDTO.getSalePointId() != null) {
            user.setSalePointId(updateDTO.getSalePointId());
        }
        if (updateDTO.getName() != null) {
            user.setName(updateDTO.getName());
        }
        if (updateDTO.getEmail() != null) {
            user.setEmail(updateDTO.getEmail());
        }
        if (updateDTO.getPassword() != null) {
            user.setPassword(updateDTO.getPassword()); // Em produção, hash a senha aqui
        }
        if (updateDTO.getDocumentsStatus() != null) {
            user.setDocumentsStatus(updateDTO.getDocumentsStatus());
        }
        if (updateDTO.getType() != null) {
            user.setType(updateDTO.getType());
        }

        User updatedUser = userRepository.save(user);
        return convertToResponseDTO(updatedUser);
    }

    /**
     * Deleta um usuário
     */
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário", "id", id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Converte User para UserResponseDTO
     * Método público para uso em outros serviços
     */
    public UserResponseDTO convertToResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .salePointId(user.getSalePointId())
                .name(user.getName())
                .email(user.getEmail())
                .documentsStatus(user.getDocumentsStatus())
                .type(user.getType())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
