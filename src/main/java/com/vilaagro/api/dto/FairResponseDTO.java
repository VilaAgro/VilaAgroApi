package com.vilaagro.api.dto;

import com.vilaagro.api.model.Fair;
import com.vilaagro.api.model.FairStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FairResponseDTO {
    private UUID id;
    private LocalDate date;
    private String startTime;
    private String endTime;
    private Integer expectedMerchants;
    private String notes;
    private FairStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static FairResponseDTO fromEntity(Fair fair) {
        return FairResponseDTO.builder()
                .id(fair.getId())
                .date(fair.getDate())
                .startTime(fair.getStartTime())
                .endTime(fair.getEndTime())
                .expectedMerchants(fair.getExpectedMerchants())
                .notes(fair.getNotes())
                .status(fair.getStatus())
                .createdAt(fair.getCreatedAt())
                .updatedAt(fair.getUpdatedAt())
                .build();
    }
}
