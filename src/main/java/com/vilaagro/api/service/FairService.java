package com.vilaagro.api.service;

import com.vilaagro.api.dto.FairResponseDTO;
import com.vilaagro.api.exception.ResourceNotFoundException;
import com.vilaagro.api.model.AccountStatus;
import com.vilaagro.api.model.Fair;
import com.vilaagro.api.model.UserType;
import com.vilaagro.api.repository.FairRepository;
import com.vilaagro.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true)
    public List<FairResponseDTO> getAllFairs() {
        // Atualiza count de merchants para todas as feiras
        updateAllFairsCount();
        
        return fairRepository.findAll()
                .stream()
                .map(FairResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FairResponseDTO> getFairsByMonthAndYear(int month, int year) {
        // Atualiza count de merchants para todas as feiras
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
        
        // Atualiza count de merchants automaticamente
        updateMerchantCount(fair);
        
        return FairResponseDTO.fromEntity(fair);
    }

    /**
     * Atualiza o count de merchants esperados baseado em usuários ativos
     */
    private void updateMerchantCount(Fair fair) {
        long activeMerchants = userRepository.findAll()
                .stream()
                .filter(u -> u.getType() != UserType.ADMIN)
                .filter(u -> u.getDocumentsStatus() == AccountStatus.ACTIVE)
                .count();
        
        fair.setExpectedMerchants((int) activeMerchants);
        fairRepository.save(fair);
    }

    /**
     * Atualiza count de merchants para todas as feiras
     */
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

    /**
     * Busca atrações de uma feira específica
     */
    @Transactional(readOnly = true)
    public List<com.vilaagro.api.dto.AttractionResponseDTO> getAttractionsByFairId(UUID fairId) {
        Fair fair = fairRepository.findById(fairId)
                .orElseThrow(() -> new ResourceNotFoundException("Feira", "id", fairId));
        
        return fair.getAttractions()
                .stream()
                .map(com.vilaagro.api.dto.AttractionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
