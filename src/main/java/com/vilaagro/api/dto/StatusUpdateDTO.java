// VilaAgroApi/src/main/java/com/vilaagro/api/dto/StatusUpdateDTO.java
package com.vilaagro.api.dto;

import com.vilaagro.api.model.AccountStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusUpdateDTO {

    @NotNull(message = "Status é obrigatório")
    private AccountStatus status;

    // Você pode adicionar este campo opcional se quiser implementar a RN-D.2.4 (fornecer motivo)
    private String reason;
}