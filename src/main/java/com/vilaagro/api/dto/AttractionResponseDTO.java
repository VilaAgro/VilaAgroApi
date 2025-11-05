// VilaAgroApi/src/main/java/com/vilaagro/api/dto/AttractionResponseDTO.java
package com.vilaagro.api.dto;

import com.vilaagro.api.model.Attraction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttractionResponseDTO {
    private UUID id;
    private LocalTime timeStart;
    private LocalTime timeEnd;
    private FairResponseDTO fair;
    private ArtistResponseDTO artist;

    public static AttractionResponseDTO fromEntity(Attraction attraction) {
        return AttractionResponseDTO.builder()
                .id(attraction.getId())
                .timeStart(attraction.getTimeStart())
                .timeEnd(attraction.getTimeEnd())
                .fair(attraction.getFair() != null ? FairResponseDTO.fromEntity(attraction.getFair()) : null)
                .artist(attraction.getArtist() != null ? ArtistResponseDTO.fromEntity(attraction.getArtist()) : null)
                .build();
    }
}