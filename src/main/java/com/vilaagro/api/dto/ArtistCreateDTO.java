package com.vilaagro.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ArtistCreateDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String name;

    private String genre;
}