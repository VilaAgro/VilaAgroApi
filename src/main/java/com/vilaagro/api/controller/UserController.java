package com.vilaagro.api.controller;

import com.vilaagro.api.dto.StatusUpdateDTO;
import com.vilaagro.api.dto.UserCreateDTO;
import com.vilaagro.api.dto.UserResponseDTO;
import com.vilaagro.api.dto.UserUpdateDTO;
import com.vilaagro.api.model.AccountStatus;
import com.vilaagro.api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST para gerenciamento de usuários
 * Implementa autorização baseada em papéis para proteção dos endpoints
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Cria um novo usuário - Apenas administradores
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserCreateDTO createDTO) {
        UserResponseDTO createdUser = userService.createUser(createDTO);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    /**
     * Lista todos os usuários - Apenas administradores
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Lista usuários com status PENDENTE - Apenas administradores
     */
    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getPendingUsers() {
        List<UserResponseDTO> pending = userService.findByDocumentsStatus(AccountStatus.PENDING);
        return ResponseEntity.ok(pending);
    }

    /**
     * Busca um usuário por ID - Usuários autenticados podem ver qualquer perfil
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable UUID id) {
        UserResponseDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Atualiza um usuário - Apenas administradores ou o próprio usuário
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or @userService.getUserById(#id).email == authentication.name")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateDTO updateDTO) {
        UserResponseDTO updatedUser = userService.updateUser(id, updateDTO);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Atualiza apenas o status dos documentos de um usuário - Apenas administradores
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserResponseDTO> updateUserStatus(
            @PathVariable UUID id,
            @Valid @RequestBody StatusUpdateDTO statusUpdateDTO
    ) {
        UserResponseDTO updated = userService.updateUserStatus(id, statusUpdateDTO);
        return ResponseEntity.ok(updated);
    }

    /**
     * Deleta um usuário - Apenas administradores
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retorna o perfil do usuário atual (a partir do token) - Usuário autenticado
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMyProfile() {
        UserResponseDTO current = userService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(current);
    }

    /**
     * Atualiza o perfil do usuário atual - Usuário autenticado
     */
    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> updateMyProfile(@Valid @RequestBody UserUpdateDTO updateDTO) {
        UserResponseDTO current = userService.getCurrentAuthenticatedUser();
        UserResponseDTO updated = userService.updateUser(current.getId(), updateDTO);
        return ResponseEntity.ok(updated);
    }
}
