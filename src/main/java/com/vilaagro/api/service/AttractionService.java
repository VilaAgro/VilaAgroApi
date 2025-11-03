package com.vilaagro.api.service;

import com.vilaagro.api.dto.AttractionCreateDTO;
import com.vilaagro.api.dto.AttractionResponseDTO;
import com.vilaagro.api.exception.ResourceNotFoundException;
import com.vilaagro.api.model.Attraction;
import com.vilaagro.api.repository.AttractionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AttractionService {

    private final AttractionRepository attractionRepository;
    private final FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public List<AttractionResponseDTO> getAllAttractions() {
        return attractionRepository.findAll()
                .stream()
                .map(AttractionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AttractionResponseDTO> getUpcomingAttractions() {
        return attractionRepository.findByDateGreaterThanEqualOrderByDateAsc(LocalDate.now())
                .stream()
                .map(AttractionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public AttractionResponseDTO createAttraction(AttractionCreateDTO createDTO, MultipartFile image) {
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = fileStorageService.storeFile(image, "attractions");
        } else if (createDTO.getImageUrl() != null) {
            imageUrl = createDTO.getImageUrl();
        }

        Attraction attraction = Attraction.builder()
                .name(createDTO.getName())
                .genre(createDTO.getGenre())
                .date(createDTO.getDate())
                .time(createDTO.getTime())
                .imageUrl(imageUrl)
                .description(createDTO.getDescription())
                .build();

        Attraction saved = attractionRepository.save(attraction);
        return AttractionResponseDTO.fromEntity(saved);
    }

    public AttractionResponseDTO updateAttraction(UUID id, AttractionCreateDTO updateDTO, MultipartFile image) {
        Attraction attraction = attractionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Atração", "id", id));

        if (image != null && !image.isEmpty()) {
            // Delete old image if exists
            if (attraction.getImageUrl() != null) {
                fileStorageService.deleteFile(attraction.getImageUrl());
            }
            attraction.setImageUrl(fileStorageService.storeFile(image, "attractions"));
        } else if (updateDTO.getImageUrl() != null) {
            attraction.setImageUrl(updateDTO.getImageUrl());
        }

        attraction.setName(updateDTO.getName());
        attraction.setGenre(updateDTO.getGenre());
        attraction.setDate(updateDTO.getDate());
        attraction.setTime(updateDTO.getTime());
        attraction.setDescription(updateDTO.getDescription());

        Attraction updated = attractionRepository.save(attraction);
        return AttractionResponseDTO.fromEntity(updated);
    }

    public void deleteAttraction(UUID id) {
        Attraction attraction = attractionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Atração", "id", id));
        
        if (attraction.getImageUrl() != null) {
            fileStorageService.deleteFile(attraction.getImageUrl());
        }
        
        attractionRepository.delete(attraction);
    }
}
