// VilaAgroApi/src/main/java/com/vilaagro/api/service/StatementService.java
package com.vilaagro.api.service;

import com.vilaagro.api.dto.StatementCreateDTO;
import com.vilaagro.api.dto.StatementResponseDTO;
import com.vilaagro.api.dto.UserResponseDTO;
import com.vilaagro.api.exception.ResourceNotFoundException;
import com.vilaagro.api.model.Statement;
import com.vilaagro.api.model.User;
import com.vilaagro.api.repository.StatementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StatementService {

    private final StatementRepository statementRepository;
    private final UserService userService; // Para converter o Admin (User) -> UserResponseDTO

    /**
     * Admin: Cria um novo comunicado (RF-D.5.2)
     */
    public StatementResponseDTO createStatement(StatementCreateDTO createDTO, User adminUser) {
        Statement statement = Statement.builder()
                .message(createDTO.getMessage())
                .stereotype(createDTO.getStereotype())
                .admin(adminUser)
                .build();

        Statement saved = statementRepository.save(statement);
        return convertToResponseDTO(saved);
    }

    /**
     * Usuário/Admin: Lista todos os comunicados (RF-C.1.3)
     */
    @Transactional(readOnly = true)
    public List<StatementResponseDTO> getAllStatements() {
        return statementRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Usuário/Admin: Busca um comunicado por ID
     */
    @Transactional(readOnly = true)
    public StatementResponseDTO getStatementById(UUID id) {
        Statement statement = statementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comunicado", "id", id));
        return convertToResponseDTO(statement);
    }

    /**
     * Admin: Atualiza um comunicado (RF-D.5.2)
     */
    public StatementResponseDTO updateStatement(UUID id, StatementCreateDTO updateDTO) {
        Statement statement = statementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comunicado", "id", id));

        // (Aqui poderiamos adicionar lógica para verificar se o admin que edita é o mesmo que criou)

        if (updateDTO.getMessage() != null) {
            statement.setMessage(updateDTO.getMessage());
        }
        if (updateDTO.getStereotype() != null) {
            statement.setStereotype(updateDTO.getStereotype());
        }

        Statement updated = statementRepository.save(statement);
        return convertToResponseDTO(updated);
    }

    /**
     * Admin: Deleta um comunicado
     */
    public void deleteStatement(UUID id) {
        if (!statementRepository.existsById(id)) {
            throw new ResourceNotFoundException("Comunicado", "id", id);
        }
        statementRepository.deleteById(id);
    }

    /**
     * Converte a entidade Statement para o DTO de resposta
     */
    private StatementResponseDTO convertToResponseDTO(Statement statement) {
        // Converte o usuário admin para DTO (para não expor a senha)
        UserResponseDTO adminDto = userService.convertToResponseDTO(statement.getAdmin());

        return StatementResponseDTO.builder()
                .id(statement.getId())
                .message(statement.getMessage())
                .stereotype(statement.getStereotype())
                .createdAt(statement.getCreatedAt())
                .updatedAt(statement.getUpdatedAt())
                .admin(adminDto)
                .build();
    }
}