package com.vilaagro.api.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO para o resumo de frequência de um comerciante (RF-C.1.3)
 */
@Data
@Builder
public class AttendanceSummaryDTO {

    private long totalAbsences;
    private long justifiedAbsences; // Faltas que foram aprovadas (isAccepted = true)
    private long pendingJustifications; // Faltas com justif. pendente (isApproved = null)
    private long unjustifiedAbsences; // Faltas reprovadas ou sem justificativa
    private long consecutiveAbsences; // Faltas consecutivas sem justificativa
    private boolean isCompliant; // Se o usuário está em conformidade (< 3 consecutivas e < 6 no ano)
}