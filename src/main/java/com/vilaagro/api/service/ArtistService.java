// VilaAgroApi/src/main/java/com/vilaagro/api/service/ArtistService.java
package com.vilaagro.api.service;

import com.vilaagro.api.dto.ArtistCreateDTO;
import com.vilaagro.api.dto.ArtistResponseDTO;
import com.vilaagro.api.exception.ResourceNotFoundException;
import com.vilaagro.api.model.Artist;
import com.vilaagro.api.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ArtistService {

    private final ArtistRepository artistRepository;

    /**
     * Cria um novo Artista, lidando com o upload do banner
     */
    public ArtistResponseDTO createArtist(ArtistCreateDTO createDTO, MultipartFile file) throws IOException {

        Artist artist = new Artist();
        artist.setName(createDTO.getName());
        artist.setGenre(createDTO.getGenre());

        // Processa o arquivo e salva os bytes
        if (file != null && !file.isEmpty()) {
            artist.setBanner(file.getBytes());
        }

        Artist savedArtist = artistRepository.save(artist);
        return convertToResponseDTO(savedArtist);
    }

    /**
     * Atualiza um Artista
     */
    public ArtistResponseDTO updateArtist(UUID id, ArtistCreateDTO updateDTO, MultipartFile file) throws IOException {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artista", "id", id));

        artist.setName(updateDTO.getName());
        artist.setGenre(updateDTO.getGenre());

        // Se um novo arquivo for enviado, ele substitui o antigo
        if (file != null && !file.isEmpty()) {
            artist.setBanner(file.getBytes());
        }

        Artist updatedArtist = artistRepository.save(artist);
        return convertToResponseDTO(updatedArtist);
    }

    /**
     * Busca os bytes do banner de um artista (para download)
     */
    @Transactional(readOnly = true)
    public byte[] getArtistBanner(UUID id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artista", "id", id));

        if (artist.getBanner() == null) {
            throw new ResourceNotFoundException("Banner", "id", id);
        }
        return artist.getBanner();
    }

    /**
     * Lista todos os artistas
     */
    @Transactional(readOnly = true)
    public List<ArtistResponseDTO> getAllArtists() {
        return artistRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Deleta um artista
     */
    public void deleteArtist(UUID id) {
        if (!artistRepository.existsById(id)) {
            throw new ResourceNotFoundException("Artista", "id", id);
        }
        artistRepository.deleteById(id);
    }

    /**
     * Converte a Entidade Artist para o DTO de Resposta
     */
    private ArtistResponseDTO convertToResponseDTO(Artist artist) {
        return ArtistResponseDTO.builder()
                .id(artist.getId())
                .name(artist.getName())
                .genre(artist.getGenre())
                .hasBanner(artist.getBanner() != null && artist.getBanner().length > 0)
                .build();
    }
}