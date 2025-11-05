// VilaAgroApi/src/main/java/com/vilaagro/api/controller/ArtistController.java
package com.vilaagro.api.controller;

import com.vilaagro.api.dto.ArtistCreateDTO;
import com.vilaagro.api.dto.ArtistResponseDTO;
import com.vilaagro.api.service.ArtistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;

    /**
     * Cria um novo Artista (Admin)
     * Aceita multipart/form-data (Campos de texto + 1 arquivo)
     */
    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ArtistResponseDTO> createArtist(
            // Recebemos o DTO como @RequestPart, mas como JSON String
            // NÃO, vamos usar @RequestParam para evitar o erro de Content-Type.

            @RequestParam("name") String name,
            @RequestParam("genre") String genre,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws IOException {

        // Monta o DTO manualmente
        ArtistCreateDTO createDTO = new ArtistCreateDTO();
        createDTO.setName(name);
        createDTO.setGenre(genre);

        ArtistResponseDTO savedArtist = artistService.createArtist(createDTO, file);
        return new ResponseEntity<>(savedArtist, HttpStatus.CREATED);
    }

    /**
     * Atualiza um Artista (Admin)
     */
    @PutMapping(value = "/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ArtistResponseDTO> updateArtist(
            @PathVariable UUID id,
            @RequestParam("name") String name,
            @RequestParam("genre") String genre,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws IOException {

        ArtistCreateDTO updateDTO = new ArtistCreateDTO();
        updateDTO.setName(name);
        updateDTO.setGenre(genre);

        ArtistResponseDTO updatedArtist = artistService.updateArtist(id, updateDTO, file);
        return ResponseEntity.ok(updatedArtist);
    }

    /**
     * Lista todos os artistas (Público)
     */
    @GetMapping
    public ResponseEntity<List<ArtistResponseDTO>> getAllArtists() {
        return ResponseEntity.ok(artistService.getAllArtists());
    }

    /**
     * Deleta um artista (Admin)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteArtist(@PathVariable UUID id) {
        artistService.deleteArtist(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint público para baixar o banner de um artista
     */
    @GetMapping("/{id}/banner")
    public ResponseEntity<byte[]> getArtistBanner(@PathVariable UUID id) {
        byte[] bannerData = artistService.getArtistBanner(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"banner.jpg\"")
                .contentType(MediaType.IMAGE_JPEG) // Assumindo JPEG
                .body(bannerData);
    }
}