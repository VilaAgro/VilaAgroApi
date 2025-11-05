package com.vilaagro.api.dto;

import com.vilaagro.api.model.Artist;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class ArtistResponseDTO {

    private UUID id;
    private String name;
    private String genre;
    private boolean hasBanner;

    public static ArtistResponseDTO fromEntity(Artist artist) {
        return ArtistResponseDTO.builder()
                .id(artist.getId())
                .name(artist.getName())
                .genre(artist.getGenre())
                .build();
    }
}