// VilaAgroApi/src/main/java/com/vilaagro/api/controller/StatementController.java
package com.vilaagro.api.controller;

import com.vilaagro.api.dto.StatementCreateDTO;
import com.vilaagro.api.dto.StatementResponseDTO;
import com.vilaagro.api.service.CustomUserDetailsService;
import com.vilaagro.api.service.StatementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST para Gerenciamento de Comunicados (RF-D.5)
 */
@RestController
@RequestMapping("/api/statements")
@RequiredArgsConstructor
public class StatementController {

    private final StatementService statementService;

    /**
     * Admin: Cria um novo comunicado (RF-D.5.2)
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<StatementResponseDTO> createStatement(
            @Valid @RequestBody StatementCreateDTO createDTO,
            @AuthenticationPrincipal CustomUserDetailsService.CustomUserPrincipal adminPrincipal
    ) {
        StatementResponseDTO created = statementService.createStatement(createDTO, adminPrincipal.getUser());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Usuário/Admin: Lista todos os comunicados (RF-C.1.3)
     * (Qualquer usuário autenticado pode ver os comunicados)
     */
    @GetMapping
    public ResponseEntity<List<StatementResponseDTO>> getAllStatements() {
        List<StatementResponseDTO> statements = statementService.getAllStatements();
        return ResponseEntity.ok(statements);
    }

    /**
     * Usuário/Admin: Busca um comunicado por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<StatementResponseDTO> getStatementById(@PathVariable UUID id) {
        StatementResponseDTO statement = statementService.getStatementById(id);
        return ResponseEntity.ok(statement);
    }

    /**
     * Admin: Atualiza um comunicado (RF-D.5.2)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<StatementResponseDTO> updateStatement(
            @PathVariable UUID id,
            @Valid @RequestBody StatementCreateDTO updateDTO
    ) {
        StatementResponseDTO updated = statementService.updateStatement(id, updateDTO);
        return ResponseEntity.ok(updated);
    }

    /**
     * Admin: Deleta um comunicado
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteStatement(@PathVariable UUID id) {
        statementService.deleteStatement(id);
        return ResponseEntity.noContent().build();
    }
}