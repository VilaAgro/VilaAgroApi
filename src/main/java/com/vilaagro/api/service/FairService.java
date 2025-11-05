package com.vilaagro.api.service;

import com.vilaagro.api.dto.AttractionResponseDTO;
import com.vilaagro.api.dto.FairResponseDTO;
import com.vilaagro.api.exception.ResourceNotFoundException;
import com.vilaagro.api.model.AccountStatus;
import com.vilaagro.api.model.Fair;
import com.vilaagro.api.model.UserType;
import com.vilaagro.api.repository.FairRepository;
import com.vilaagro.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.vilaagro.api.dto.FairCreateDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FairService {

    private final FairRepository fairRepository;
    private final UserRepository userRepository;

    public FairResponseDTO createFair(FairCreateDTO createDTO) {
        Fair fair = Fair.builder()
                .date(createDTO.getDate())
                .startTime(createDTO.getStartTime())
                .endTime(createDTO.getEndTime())
                .notes(createDTO.getNotes())
                .status(createDTO.getStatus())
                .expectedMerchants(0) // <-- CORREÇÃO: Define o valor padrão
                .attractions(new java.util.ArrayList<>()) // <-- ADIÇÃO: Evita NullPointerException
                .build();

        Fair savedFair = fairRepository.save(fair);
        return FairResponseDTO.fromEntity(savedFair);
    }

    /**
     * Admin: Atualiza uma feira
     */
    public FairResponseDTO updateFair(UUID id, FairCreateDTO updateDTO) {
        Fair fair = fairRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feira", "id", id));

        fair.setDate(updateDTO.getDate());
        fair.setStartTime(updateDTO.getStartTime());
        fair.setEndTime(updateDTO.getEndTime());
        fair.setNotes(updateDTO.getNotes());
        fair.setStatus(updateDTO.getStatus());

        Fair updatedFair = fairRepository.save(fair);
        return FairResponseDTO.fromEntity(updatedFair);
    }

    /**
     * Admin: Deleta uma feira
     */
    public void deleteFair(UUID id) {
        Fair fair = fairRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feira", "id", id));

        // Regra de Negócio: Não deletar feira se ela tiver atrações vinculadas
        if (fair.getAttractions() != null && !fair.getAttractions().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Não é possível excluir uma feira com atrações vinculadas.");
        }

        fairRepository.delete(fair);
    }

    @Transactional(readOnly = true)
    public List<FairResponseDTO> getAllFairs() {
        updateAllFairsCount();

        return fairRepository.findAll()
                .stream()
                .map(FairResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FairResponseDTO> getFairsByMonthAndYear(int month, int year) {
        updateAllFairsCount();

        return fairRepository.findByMonthAndYear(month, year)
                .stream()
                .map(FairResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FairResponseDTO getNextFair() {
        Fair fair = fairRepository.findFirstByDateGreaterThanEqualOrderByDateAsc(LocalDate.now())
                .orElseThrow(() -> new ResourceNotFoundException("Feira", "próxima", "não encontrada"));

        updateMerchantCount(fair);

        return FairResponseDTO.fromEntity(fair);
    }

    private void updateMerchantCount(Fair fair) {
        long activeMerchants = userRepository.findAll()
                .stream()
                .filter(u -> u.getType() != UserType.ADMIN)
                .filter(u -> u.getDocumentsStatus() == AccountStatus.ACTIVE)
                .count();

        fair.setExpectedMerchants((int) activeMerchants);
        fairRepository.save(fair);
    }

    private void updateAllFairsCount() {
        long activeMerchants = userRepository.findAll()
                .stream()
                .filter(u -> u.getType() != UserType.ADMIN)
                .filter(u -> u.getDocumentsStatus() == AccountStatus.ACTIVE)
                .count();

        List<Fair> allFairs = fairRepository.findAll();
        allFairs.forEach(fair -> fair.setExpectedMerchants((int) activeMerchants));
        fairRepository.saveAll(allFairs);
    }

    @Transactional(readOnly = true)
    public List<AttractionResponseDTO> getAttractionsByFairId(UUID fairId) {
        Fair fair = fairRepository.findById(fairId)
                .orElseThrow(() -> new ResourceNotFoundException("Feira", "id", fairId));

        return fair.getAttractions()
                .stream()
                .map(AttractionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}