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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalTime;
import java.util.UUID;

import java.util.List;

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
    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<AttractionResponseDTO> createAttraction(
            // Em vez de @RequestPart("attraction") AttractionCreateDTO dto,
            // use @RequestParam para cada campo:
            @RequestParam("fair_id") UUID fairId,
            @RequestParam("artist_id") UUID artistId,
            @RequestParam("time_start") LocalTime timeStart,
            @RequestParam("time_end") LocalTime timeEnd,

            // Opcional: O 'banner' do artista
            // Se o arquivo for o banner do ARTISTA, ele deveria ser enviado
            // no CRUD de Artista, não aqui.
            // Se for um banner da ATRAÇÃO, o schema do db.sql não tem um campo para isso.

            // Vamos assumir que o 'file' é o 'banner' do Artista (mesmo que seja estranho aqui):
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {

        // 1. Crie o DTO manualmente aqui dentro
        AttractionCreateDTO createDTO = new AttractionCreateDTO();
        createDTO.setFairId(fairId);
        createDTO.setArtistId(artistId);
        createDTO.setTimeStart(timeStart);
        createDTO.setTimeEnd(timeEnd);

        // 2. Chame seu serviço
        // (O 'file' provavelmente deveria ir para o ArtistService, não o AttractionService)
        AttractionResponseDTO newAttraction = attractionService.createAttraction(createDTO, file);

        return new ResponseEntity<>(newAttraction, HttpStatus.CREATED);
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
