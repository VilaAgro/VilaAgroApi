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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Controller para atrações/shows da feira
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
     */
    @PostMapping(consumes = {"multipart/form-data"})
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AttractionResponseDTO> createAttraction(
            @Valid @RequestPart("attraction") AttractionCreateDTO createDTO,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        AttractionResponseDTO created = attractionService.createAttraction(createDTO, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Atualiza uma atração - Admin
     */
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AttractionResponseDTO> updateAttraction(
            @PathVariable UUID id,
            @Valid @RequestPart("attraction") AttractionCreateDTO updateDTO,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        AttractionResponseDTO updated = attractionService.updateAttraction(id, updateDTO, image);
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
