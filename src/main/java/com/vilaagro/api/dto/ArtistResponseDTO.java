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
    private String bannerUrl;

    public static ArtistResponseDTO fromEntity(Artist artist) {
        boolean hasBanner = artist.getBanner() != null && artist.getBanner().length > 0;
        String bannerUrl = hasBanner ? "/api/artists/" + artist.getId() + "/banner" : null;

        return ArtistResponseDTO.builder()
                .id(artist.getId())
                .name(artist.getName())
                .genre(artist.getGenre())
                .hasBanner(hasBanner)
                .bannerUrl(bannerUrl)
                .build();
    }
}