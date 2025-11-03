// VilaAgroApi/src/main/java/com/vilaagro/api/service/DashboardService.java
package com.vilaagro.api.service;

import com.vilaagro.api.dto.DashboardStatsDTO;
import com.vilaagro.api.model.AccountStatus;
import com.vilaagro.api.repository.JustificationRepository;
import com.vilaagro.api.repository.SalePointRepository;
import com.vilaagro.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço para buscar dados e estatísticas para o Dashboard do Admin
 * (RF-D.1)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final UserRepository userRepository;
    private final SalePointRepository salePointRepository;
    private final JustificationRepository justificationRepository;

    public DashboardStatsDTO getDashboardStats() {

        // 1. Contador de "Solicitações Pendentes" (RF-D.1.1)
        long pendingRegs = userRepository.countByDocumentsStatus(AccountStatus.PENDING);

        // 2. Contador de "Fila de Espera" (RF-D.1.2)
        long waitingList = userRepository.countByDocumentsStatus(AccountStatus.APPROVED);

        // 3. Contador de "Justificativas a Analisar" (RF-D.1.5)
        long pendingJustifs = justificationRepository.countByIsApprovedIsNull();

        // 4. Taxa de Ocupação (RF-D.1.3)
        long totalSpots = salePointRepository.count();
        long occupiedSpots = userRepository.countByDocumentsStatus(AccountStatus.ACTIVE);

        double occupationRate = 0.0;
        if (totalSpots > 0) {
            occupationRate = ((double) occupiedSpots / totalSpots) * 100.0;
        }

        return DashboardStatsDTO.builder()
                .pendingRegistrations(pendingRegs)
                .waitingList(waitingList)
                .pendingJustifications(pendingJustifs)
                .totalSalePoints(totalSpots)
                .occupiedSalePoints(occupiedSpots)
                .occupationRate(occupationRate)
                .build();
    }
}