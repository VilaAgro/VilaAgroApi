package com.vilaagro.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para atualização de cursos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseUpdateDTO {

    private UUID addressId;
    private String title;
    private String description;
    private LocalDateTime datetime;
}
