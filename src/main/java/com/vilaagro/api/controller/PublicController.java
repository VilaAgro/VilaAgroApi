// VilaAgroApi/src/main/java/com/vilaagro/api/controller/PublicController.java
package com.vilaagro.api.controller;

import com.vilaagro.api.dto.UserResponseDTO;
import com.vilaagro.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST para endpoints da "Área Pública"
 * Não requer autenticação (RF-A)
 */
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final UserService userService;

    /**
     * Lista todos os comerciantes ATIVOS (RF-A.2.1)
     */
    @GetMapping("/merchants")
    public ResponseEntity<List<UserResponseDTO>> getActiveMerchants() {
        List<UserResponseDTO> merchants = userService.getAllActiveMerchants();
        return ResponseEntity.ok(merchants);
    }

    /**
     * Exibe o perfil público de um comerciante ATIVO (RF-A.3)
     */
    @GetMapping("/merchants/{id}")
    public ResponseEntity<UserResponseDTO> getActiveMerchantProfile(@PathVariable UUID id) {
        UserResponseDTO merchant = userService.getActiveMerchantById(id);
        return ResponseEntity.ok(merchant);
    }

    // NOTA: Você também pode mover os endpoints públicos
    // de Cursos (GET /courses) e Pontos de Venda (GET /api/sale-points)
    // para este controller para centralizar toda a área pública.
}