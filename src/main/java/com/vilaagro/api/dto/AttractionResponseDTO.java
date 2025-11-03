package com.vilaagro.api.dto;

import com.vilaagro.api.model.Attraction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttractionResponseDTO {
    private UUID id;
    private String name;
    private String genre;
    private LocalDate date;
    private String time;
    private String imageUrl;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AttractionResponseDTO fromEntity(Attraction attraction) {
        return AttractionResponseDTO.builder()
                .id(attraction.getId())
                .name(attraction.getName())
                .genre(attraction.getGenre())
                .date(attraction.getDate())
                .time(attraction.getTime())
                .imageUrl(attraction.getImageUrl())
                .description(attraction.getDescription())
                .createdAt(attraction.getCreatedAt())
                .updatedAt(attraction.getUpdatedAt())
                .build();
    }
}
