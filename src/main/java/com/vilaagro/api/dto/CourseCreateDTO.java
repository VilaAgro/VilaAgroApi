package com.vilaagro.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para criação de novos cursos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseCreateDTO {

    private UUID addressId;

    @NotBlank(message = "Título é obrigatório")
    private String title;

    private String description;

    @NotNull(message = "Data e hora são obrigatórias")
    private LocalDateTime datetime;
}
