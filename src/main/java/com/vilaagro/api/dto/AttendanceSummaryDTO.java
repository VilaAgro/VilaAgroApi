package com.vilaagro.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resumo de frequência do usuário
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceSummaryDTO {
    
    private Integer totalAbsences;
    private Integer justifiedAbsences;
    private Integer unjustifiedAbsences;
    private Integer consecutiveAbsences;
    private Boolean isCompliant;
}
