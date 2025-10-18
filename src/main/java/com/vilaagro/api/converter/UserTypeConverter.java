package com.vilaagro.api.converter;

import com.vilaagro.api.model.UserType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Conversor para mapear UserType para PostgreSQL enum
 */
@Converter(autoApply = true)
public class UserTypeConverter implements AttributeConverter<UserType, String> {

    @Override
    public String convertToDatabaseColumn(UserType attribute) {
        if (attribute == null) {
            return null;
        }

        switch (attribute) {
            case ADMIN: return "Admin";
            case PRODUTOR_RURAL: return "Produtor Rural";
            case GASTRONOMO: return "Gastronomo";
            case PRODUTOR_ARTESANAL: return "Produtor Artesanal";
            default: return "Produtor Rural";
        }
    }

    @Override
    public UserType convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return null;
        }
        // Mapeia dos valores do PostgreSQL para o enum Java
        switch (dbData) {
            case "Admin": return UserType.ADMIN;
            case "Produtor Rural": return UserType.PRODUTOR_RURAL;
            case "Gastronomo": return UserType.GASTRONOMO;
            case "Produtor Artesanal": return UserType.PRODUTOR_ARTESANAL;
            default: return UserType.PRODUTOR_RURAL;
        }
    }
}
