package com.vilaagro.api.service;

import com.vilaagro.api.dto.AttractionCreateDTO;
import com.vilaagro.api.dto.AttractionResponseDTO;
import com.vilaagro.api.dto.ArtistResponseDTO; // DTO que criamos
import com.vilaagro.api.dto.FairResponseDTO;
import com.vilaagro.api.exception.ResourceNotFoundException;
import com.vilaagro.api.model.Artist;
import com.vilaagro.api.model.Attraction;
import com.vilaagro.api.model.Fair;
import com.vilaagro.api.repository.ArtistRepository; // Repositório que criamos
import com.vilaagro.api.repository.AttractionRepository;
import com.vilaagro.api.repository.FairRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// Remova o import do MultipartFile, se houver

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AttractionService {

    private final AttractionRepository attractionRepository;
    private final FairRepository fairRepository;
    private final ArtistRepository artistRepository;

    /**
     * Cria uma nova atração (agendamento)
     */
    public AttractionResponseDTO createAttraction(AttractionCreateDTO createDTO) {

        // 1. Validar as Foreign Keys
        Fair fair = fairRepository.findById(createDTO.getFairId())
                .orElseThrow(() -> new ResourceNotFoundException("Feira (Fair)", "id", createDTO.getFairId()));

        Artist artist = artistRepository.findById(createDTO.getArtistId())
                .orElseThrow(() -> new ResourceNotFoundException("Artista", "id", createDTO.getArtistId()));

        // 2. Criar a Entidade
        Attraction attraction = new Attraction();
        attraction.setFair(fair);
        attraction.setArtist(artist);
        attraction.setTimeStart(createDTO.getTimeStart());
        attraction.setTimeEnd(createDTO.getTimeEnd());

        // 3. Salvar
        Attraction savedAttraction = attractionRepository.save(attraction);

        // 4. Retornar
        //    *** ESTA É A CORREÇÃO ***
        // Trocamos o método quebrado pelo método estático null-safe
        return AttractionResponseDTO.fromEntity(savedAttraction);
    }

    /**
     * Atualiza uma atração (agendamento)
     */
    public AttractionResponseDTO updateAttraction(UUID id, AttractionCreateDTO updateDTO) {
        // 1. Encontrar a atração existente
        Attraction attraction = attractionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Atração", "id", id));

        // 2. Validar as novas Foreign Keys (se elas mudaram)
        if (!attraction.getFair().getId().equals(updateDTO.getFairId())) {
            Fair fair = fairRepository.findById(updateDTO.getFairId())
                    .orElseThrow(() -> new ResourceNotFoundException("Feira (Fair)", "id", updateDTO.getFairId()));
            attraction.setFair(fair);
        }

        if (!attraction.getArtist().getId().equals(updateDTO.getArtistId())) {
            Artist artist = artistRepository.findById(updateDTO.getArtistId())
                    .orElseThrow(() -> new ResourceNotFoundException("Artista", "id", updateDTO.getArtistId()));
            attraction.setArtist(artist);
        }

        // 3. Atualizar os horários
        attraction.setTimeStart(updateDTO.getTimeStart());
        attraction.setTimeEnd(updateDTO.getTimeEnd());

        // 4. Salvar
        Attraction updatedAttraction = attractionRepository.save(attraction);

        // 5. Retornar
        //    *** ESTA É A CORREÇÃO ***
        return AttractionResponseDTO.fromEntity(updatedAttraction);
    }

    // --- Métodos de Listagem (necessários para o Controller) ---

    @Transactional(readOnly = true)
    public List<AttractionResponseDTO> getAllAttractions() {
        return attractionRepository.findAll().stream()
                //    *** ESTA É A CORREÇÃO ***
                .map(AttractionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AttractionResponseDTO> getUpcomingAttractions() {
        // Busca feiras futuras e, a partir delas, as atrações
        LocalDate today = LocalDate.now();
        List<Fair> upcomingFairs = fairRepository.findByDateGreaterThanEqualOrderByDateAsc(today);

        return upcomingFairs.stream()
                .flatMap(fair -> fair.getAttractions().stream()) // Pega as atrações de cada feira
                //    *** ESTA É A CORREÇÃO ***
                .map(AttractionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // --- Método de Delete (necessário para o Controller) ---

    public void deleteAttraction(UUID id) {
        if (!attractionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Atração", "id", id);
        }
        attractionRepository.deleteById(id);
    }


    /**
     * REMOVIDO: Este método privado estava causando o NullPointerException
     *
     private AttractionResponseDTO convertToResponseDTO(Attraction attraction) {
     // ... (código quebrado)
     }
     */
}