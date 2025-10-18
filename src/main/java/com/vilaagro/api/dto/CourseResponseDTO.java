package com.vilaagro.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para resposta com dados do curso
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponseDTO {

    private UUID id;
    private UUID addressId;
    private String title;
    private String description;
    private LocalDateTime datetime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
