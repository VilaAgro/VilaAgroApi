// VilaAgroApi/src/main/java/com/vilaagro/api/dto/AttractionResponseDTO.java
package com.vilaagro.api.dto;

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

    // Detalhes da Feira (Assumindo que FairResponseDTO existe)
    private FairResponseDTO fair;

    // Detalhes do Artista (Este DTO foi criado na etapa anterior)
    private ArtistResponseDTO artist;
}