// VilaAgroApi/src/main/java/com/vilaagro/api/dto/AttractionCreateDTO.java
package com.vilaagro.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.UUID;

/**
 * DTO para criar uma nova Atração (Agendamento).
 * Este DTO está condizente com a tabela 'attraction' do db.sql
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttractionCreateDTO {

    @NotNull(message = "O ID da Feira (fair_id) é obrigatório")
    private UUID fairId;

    @NotNull(message = "O ID do Artista (artist_id) é obrigatório")
    private UUID artistId;

    @NotNull(message = "A hora de início (time_start) é obrigatória")
    private LocalTime timeStart;

    @NotNull(message = "A hora de término (time_end) é obrigatória")
    private LocalTime timeEnd;
}