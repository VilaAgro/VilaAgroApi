package com.vilaagro.api.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO para carregar os widgets e estatísticas do Dashboard do Admin
 * (RF-D.1)
 */
@Data
@Builder
public class DashboardStatsDTO {

    // RF-D.1.1: Contador de "Solicitações Pendentes"
    private long pendingRegistrations;

    // RF-D.1.2: Contador de "Fila de Espera"
    private long waitingList;

    // RF-D.1.5: Contador de "Justificativas a Analisar"
    private long pendingJustifications;

    // RF-D.1.3: Gráfico de "Taxa de Ocupação"
    private long totalSalePoints;
    private long occupiedSalePoints;
    private double occupationRate;
}