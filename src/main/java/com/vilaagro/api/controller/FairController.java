package com.vilaagro.api.controller;

import com.vilaagro.api.dto.AttractionResponseDTO;
import com.vilaagro.api.dto.FairResponseDTO;
import com.vilaagro.api.service.FairService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller para feiras/eventos
 */
@RestController
@RequestMapping("/api/fairs")
@RequiredArgsConstructor
public class FairController {

    private final FairService fairService;

    /**
     * Lista todas as feiras ou filtra por mês/ano - Público
     */
    @GetMapping
    public ResponseEntity<List<FairResponseDTO>> getFairs(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year
    ) {
        if (month != null && year != null) {
            List<FairResponseDTO> fairs = fairService.getFairsByMonthAndYear(month, year);
            return ResponseEntity.ok(fairs);
        }
        List<FairResponseDTO> fairs = fairService.getAllFairs();
        return ResponseEntity.ok(fairs);
    }

    /**
     * Busca próxima feira agendada - Público
     */
    @GetMapping("/next")
    public ResponseEntity<FairResponseDTO> getNextFair() {
        FairResponseDTO fair = fairService.getNextFair();
        return ResponseEntity.ok(fair);
    }

    /**
     * Lista atrações de uma feira específica - Público
     */
    @GetMapping("/{id}/attractions")
    public ResponseEntity<List<AttractionResponseDTO>> getFairAttractions(@PathVariable UUID id) {
        List<AttractionResponseDTO> attractions = fairService.getAttractionsByFairId(id);
        return ResponseEntity.ok(attractions);
    }
}
