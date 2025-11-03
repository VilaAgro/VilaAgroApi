package com.vilaagro.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttractionCreateDTO {
    
    @NotBlank(message = "Nome é obrigatório")
    private String name;
    
    @NotBlank(message = "Gênero é obrigatório")
    private String genre;
    
    @NotNull(message = "Data é obrigatória")
    private LocalDate date;
    
    @NotBlank(message = "Horário é obrigatório")
    private String time;
    
    private String imageUrl;
    
    private String description;
}
