package com.vilaagro.api.controller;

import com.vilaagro.api.dto.AuthResponseDTO;
import com.vilaagro.api.dto.LoginRequestDTO;
import com.vilaagro.api.dto.UserCreateDTO;
import com.vilaagro.api.dto.UserResponseDTO;
import com.vilaagro.api.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsável pelos endpoints de autenticação
 * Gerencia registro, login e logout de usuários
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint para registro de novos usuários
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(
            @Valid @RequestBody UserCreateDTO createDTO,
            HttpServletResponse response
    ) {
        try {
            log.info("Tentativa de registro para email: {}", createDTO.getEmail());

            UserResponseDTO user = authService.registerUser(createDTO, response);

            AuthResponseDTO authResponse = AuthResponseDTO.builder()
                    .message("Usuário registrado e autenticado com sucesso")
                    .user(user)
                    .success(true)
                    .build();

            log.info("Usuário registrado com sucesso: {}", createDTO.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);

        } catch (Exception e) {
            log.error("Erro durante registro: {}", e.getMessage());

            AuthResponseDTO errorResponse = AuthResponseDTO.builder()
                    .message(e.getMessage())
                    .success(false)
                    .build();

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Endpoint para login de usuários
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO loginRequest,
            HttpServletResponse response
    ) {
        try {
            log.info("Tentativa de login para email: {}", loginRequest.getEmail());

            UserResponseDTO user = authService.authenticateUser(loginRequest, response);

            AuthResponseDTO authResponse = AuthResponseDTO.builder()
                    .message("Login realizado com sucesso")
                    .user(user)
                    .success(true)
                    .build();

            log.info("Login realizado com sucesso para: {}", loginRequest.getEmail());
            return ResponseEntity.ok(authResponse);

        } catch (Exception e) {
            log.error("Erro durante login: {}", e.getMessage());

            AuthResponseDTO errorResponse = AuthResponseDTO.builder()
                    .message("Credenciais inválidas")
                    .success(false)
                    .build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    /**
     * Endpoint para logout de usuários
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<AuthResponseDTO> logout(HttpServletResponse response) {
        try {
            authService.logoutUser(response);

            AuthResponseDTO authResponse = AuthResponseDTO.builder()
                    .message("Logout realizado com sucesso")
                    .success(true)
                    .build();

            return ResponseEntity.ok(authResponse);

        } catch (Exception e) {
            log.error("Erro durante logout: {}", e.getMessage());

            AuthResponseDTO errorResponse = AuthResponseDTO.builder()
                    .message("Erro interno do servidor")
                    .success(false)
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Endpoint para refresh do token de acesso
     * POST /api/auth/refresh
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refreshToken(HttpServletResponse response) {
        try {
            authService.refreshAccessToken(response);

            AuthResponseDTO authResponse = AuthResponseDTO.builder()
                    .message("Token renovado com sucesso")
                    .success(true)
                    .build();

            return ResponseEntity.ok(authResponse);

        } catch (Exception e) {
            log.error("Erro durante refresh do token: {}", e.getMessage());

            AuthResponseDTO errorResponse = AuthResponseDTO.builder()
                    .message("Token de refresh inválido")
                    .success(false)
                    .build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    /**
     * Endpoint para verificar o status de autenticação atual
     * GET /api/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<AuthResponseDTO> getCurrentUser() {
        try {
            UserResponseDTO currentUser = authService.getCurrentUser();

            AuthResponseDTO response = AuthResponseDTO.builder()
                    .message("Usuário autenticado")
                    .user(currentUser)
                    .success(true)
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erro ao buscar usuário atual: {}", e.getMessage());

            AuthResponseDTO errorResponse = AuthResponseDTO.builder()
                    .message("Usuário não autenticado")
                    .success(false)
                    .build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
}
