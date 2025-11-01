// VilaAgroApi/src/main/java/com/vilaagro/api/controller/SalePointController.java
package com.vilaagro.api.controller;

import com.vilaagro.api.dto.SalePointAllocateDTO;
import com.vilaagro.api.dto.SalePointCreateDTO;
import com.vilaagro.api.dto.SalePointResponseDTO;
import com.vilaagro.api.dto.UserResponseDTO;
import com.vilaagro.api.service.SalePointService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST para Gerenciamento de Pontos de Venda (RF-D.4)
 * Protegido para acesso apenas de Administradores
 */
@RestController
@RequestMapping("/api/sale-points")
@RequiredArgsConstructor
public class SalePointController {

    private final SalePointService salePointService;

    /**
     * Cria um novo Ponto de Venda (RF-D.4.2)
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<SalePointResponseDTO> createSalePoint(@Valid @RequestBody SalePointCreateDTO createDTO) {
        SalePointResponseDTO created = salePointService.createSalePoint(createDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Lista todos os Pontos de Venda (RF-D.4.1)
     * (Público, para RF-A.3.4)
     */
    @GetMapping
    public ResponseEntity<List<SalePointResponseDTO>> getAllSalePoints() {
        List<SalePointResponseDTO> salePoints = salePointService.getAllSalePoints();
        return ResponseEntity.ok(salePoints);
    }

    /**
     * Busca um Ponto de Venda por ID
     * (Público, para RF-A.3.4)
     */
    @GetMapping("/{id}")
    public ResponseEntity<SalePointResponseDTO> getSalePointById(@PathVariable UUID id) {
        SalePointResponseDTO salePoint = salePointService.getSalePointById(id);
        return ResponseEntity.ok(salePoint);
    }

    /**
     * Atualiza um Ponto de Venda (RF-D.4.2)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<SalePointResponseDTO> updateSalePoint(
            @PathVariable UUID id,
            @Valid @RequestBody SalePointCreateDTO updateDTO
    ) {
        SalePointResponseDTO updated = salePointService.updateSalePoint(id, updateDTO);
        return ResponseEntity.ok(updated);
    }

    /**
     * Deleta um Ponto de Venda (RF-D.4.2)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteSalePoint(@PathVariable UUID id) {
        salePointService.deleteSalePoint(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Aloca um usuário a um Ponto de Venda (RF-D.4.4)
     */
    @PostMapping("/{id}/allocate")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserResponseDTO> allocateUser(
            @PathVariable UUID id,
            @Valid @RequestBody SalePointAllocateDTO allocateDTO
    ) {
        UserResponseDTO allocatedUser = salePointService.allocateUserToSalePoint(id, allocateDTO);
        return ResponseEntity.ok(allocatedUser);
    }

    /**
     * Remove (desaloca) um usuário de um Ponto de Venda
     */
    @DeleteMapping("/{id}/allocate")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserResponseDTO> removeUser(@PathVariable UUID id) {
        UserResponseDTO removedUser = salePointService.removeUserFromSalePoint(id);
        return ResponseEntity.ok(removedUser);
    }
}