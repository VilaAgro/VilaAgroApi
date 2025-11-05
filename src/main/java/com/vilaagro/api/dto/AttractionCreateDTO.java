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
 * [CORRIGIDO] Este DTO agora corresponde à tabela 'attraction' do db.sql
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttractionCreateDTO {

    @NotNull(message = "O ID da Feira (fairId) é obrigatório")
    private UUID fairId;

    @NotNull(message = "O ID do Artista (artistId) é obrigatório")
    private UUID artistId;

    @NotNull(message = "A hora de início (timeStart) é obrigatória")
    private LocalTime timeStart;

    @NotNull(message = "A hora de término (timeEnd) é obrigatória")
    private LocalTime timeEnd;

}