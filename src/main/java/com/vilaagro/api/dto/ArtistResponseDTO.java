package com.vilaagro.api.dto;

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
}