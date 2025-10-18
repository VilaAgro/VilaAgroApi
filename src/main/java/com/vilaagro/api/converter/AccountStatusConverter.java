package com.vilaagro.api.converter;

import com.vilaagro.api.model.AccountStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Conversor para mapear AccountStatus para PostgreSQL enum
 */
@Converter(autoApply = true)
public class AccountStatusConverter implements AttributeConverter<AccountStatus, String> {

    @Override
    public String convertToDatabaseColumn(AccountStatus attribute) {
        if (attribute == null) {
            return null;
        }
        // Mapeia para os valores exatos do PostgreSQL enum
        switch (attribute) {
            case PENDING: return "Pending";
            case APPROVED: return "Approved";
            case ACTIVE: return "Active";
            case DISAPPROVED: return "Disapproved";
            case INACTIVE: return "Inactive";
            default: return "Pending";
        }
    }

    @Override
    public AccountStatus convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return null;
        }
        // Mapeia dos valores do PostgreSQL para o enum Java
        switch (dbData) {
            case "Pending": return AccountStatus.PENDING;
            case "Approved": return AccountStatus.APPROVED;
            case "Active": return AccountStatus.ACTIVE;
            case "Disapproved": return AccountStatus.DISAPPROVED;
            case "Inactive": return AccountStatus.INACTIVE;
            default: return AccountStatus.PENDING;
        }
    }
}
