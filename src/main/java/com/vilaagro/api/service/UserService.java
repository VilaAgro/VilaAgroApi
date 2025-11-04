package com.vilaagro.api.service;

import com.vilaagro.api.dto.TerminationRequestDTO;
import com.vilaagro.api.dto.UserCreateDTO;
import com.vilaagro.api.dto.UserResponseDTO;
import com.vilaagro.api.dto.UserUpdateDTO;
import com.vilaagro.api.dto.StatusUpdateDTO;
import com.vilaagro.api.exception.EmailAlreadyExistsException;
import com.vilaagro.api.exception.ResourceNotFoundException;
import com.vilaagro.api.model.User;
import com.vilaagro.api.model.AccountStatus;
import com.vilaagro.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
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
@Slf4j
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

        // ***** CORREÇÃO AQUI *****
        // A senha DEVE ser criptografada aqui, dentro do UserService.
        String hashedPassword = passwordEncoder.encode(createDTO.getPassword());
        // ***** FIM DA CORREÇÃO *****

        User user = User.builder()
                .salePointId(createDTO.getSalePointId())
                .name(createDTO.getName())
                .email(createDTO.getEmail())
                .password(hashedPassword) // <-- Usamos a senha criptografada
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
     * Lista usuários por status de documentos
     */
    @Transactional(readOnly = true)
    public List<UserResponseDTO> findByDocumentsStatus(AccountStatus status) {
        return userRepository.findByDocumentsStatus(status)
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
            // Hash da senha quando atualizada
            user.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
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
     * Atualiza apenas o status dos documentos de um usuário (usado por administradores)
     */
    public UserResponseDTO updateUserStatus(UUID id, StatusUpdateDTO statusUpdateDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", id));

        user.setDocumentsStatus(statusUpdateDTO.getStatus());
        // Comentário e histórico podem ser implementados posteriormente

        User saved = userRepository.save(user);
        return convertToResponseDTO(saved);
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
     * Processa solicitação de desligamento do usuário
     * Atualiza o status para INACTIVE
     */
    public void submitTermination(UUID userId, TerminationRequestDTO terminationRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", userId));

        // Atualiza status para inativo
        user.setDocumentsStatus(AccountStatus.INACTIVE);
        
        // Log da solicitação
        System.out.println("Solicitação de desligamento recebida:");
        System.out.println("Usuário: " + user.getName() + " (" + user.getEmail() + ")");
        System.out.println("Motivo: " + terminationRequest.getReason());
        if (terminationRequest.getDetails() != null) {
            System.out.println("Detalhes: " + terminationRequest.getDetails());
        }
        
        userRepository.save(user);
        
        // TODO: Implementar notificação para admin
        // TODO: Implementar registro de histórico
    }

    /**
     * Obtém o usuário atualmente autenticado a partir do SecurityContext
     */
    @Transactional(readOnly = true)
    public UserResponseDTO getCurrentAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof com.vilaagro.api.service.CustomUserDetailsService.CustomUserPrincipal) {
            com.vilaagro.api.service.CustomUserDetailsService.CustomUserPrincipal custom =
                    (com.vilaagro.api.service.CustomUserDetailsService.CustomUserPrincipal) principal;
            return convertToResponseDTO(custom.getUser());
        }
        throw new RuntimeException("Usuário não autenticado");
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllActiveMerchants() {
        return userRepository.findByDocumentsStatus(AccountStatus.ACTIVE)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca um perfil público de comerciante por ID,
     * somente se ele estiver ATIVO (RF-A.3)
     */
    @Transactional(readOnly = true)
    public UserResponseDTO getActiveMerchantById(UUID id) {
        User user = userRepository.findByIdAndDocumentsStatus(id, AccountStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Comerciante", "id", id));

        return convertToResponseDTO(user);
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
                .cpf(user.getCpf())
                .documentsStatus(user.getDocumentsStatus())
                .type(user.getType())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public UserResponseDTO requestTermination(User currentUser, TerminationRequestDTO terminationDTO) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", currentUser.getId()));

        // RN-C.5.2: Atualiza o status
        user.setDocumentsStatus(AccountStatus.REQUESTED_TERMINATION);

        // Remove o usuário do ponto de venda, se estiver alocado
        user.setSalePointId(null);

        // RN-C.5.2: Registra o motivo (log)
        log.info("Usuário {} solicitou desligamento. Motivo: {}", user.getEmail(), terminationDTO.getReason());

        // (Se houvesse um campo 'termination_reason' na tabela user, seria setado aqui)

        User updatedUser = userRepository.save(user);
        return convertToResponseDTO(updatedUser);
    }
}
