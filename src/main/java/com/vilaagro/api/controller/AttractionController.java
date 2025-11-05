// VilaAgroApi/src/main/java/com/vilaagro/api/controller/AttractionController.java
package com.vilaagro.api.controller;

import com.vilaagro.api.dto.AttractionCreateDTO;
import com.vilaagro.api.dto.AttractionResponseDTO;
import com.vilaagro.api.service.AttractionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
// Remova todos os imports de MultipartFile, MediaType, RequestParam

import java.util.List;
import java.util.UUID;

/**
 * Controller para atrações/shows da feira (Agendamentos)
 * [CORRIGIDO: Condizente com o db.sql, aceita apenas JSON]
 */
@RestController
@RequestMapping("/api/attractions")
@RequiredArgsConstructor
public class AttractionController {

    private final AttractionService attractionService;

    /**
     * Lista todas as atrações - Público
     */
    @GetMapping
    public ResponseEntity<List<AttractionResponseDTO>> getAllAttractions() {
        List<AttractionResponseDTO> attractions = attractionService.getAllAttractions();
        return ResponseEntity.ok(attractions);
    }

    /**
     * Lista próximas atrações (futuras) - Público
     */
    @GetMapping("/upcoming")
    public ResponseEntity<List<AttractionResponseDTO>> getUpcomingAttractions() {
        List<AttractionResponseDTO> attractions = attractionService.getUpcomingAttractions();
        return ResponseEntity.ok(attractions);
    }

    /**
     * Cria uma nova atração - Admin
     * [CORRIGIDO: Removido 'consumes' e '@RequestParam'. Usa '@RequestBody' JSON padrão]
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AttractionResponseDTO> createAttraction(
            @Valid @RequestBody AttractionCreateDTO createDTO
    ) {
        // O DTO
        // já contém fairId, artistId, timeStart, timeEnd.

        // O serviço agora só recebe o DTO, sem arquivo.
        AttractionResponseDTO newAttraction = attractionService.createAttraction(createDTO);

        return new ResponseEntity<>(newAttraction, HttpStatus.CREATED);
    }

    /**
     * Atualiza uma atração - Admin
     * [CORRIGIDO: Removido 'consumes' e '@RequestPart'. Usa '@RequestBody' JSON padrão]
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AttractionResponseDTO> updateAttraction(
            @PathVariable UUID id,
            @Valid @RequestBody AttractionCreateDTO updateDTO
    ) {
        // O serviço agora só recebe o DTO, sem arquivo.
        AttractionResponseDTO updated = attractionService.updateAttraction(id, updateDTO);
        return ResponseEntity.ok(updated);
    }

    /**
     * Deleta uma atração - Admin
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteAttraction(@PathVariable UUID id) {
        attractionService.deleteAttraction(id);
        return ResponseEntity.noContent().build();
    }
}