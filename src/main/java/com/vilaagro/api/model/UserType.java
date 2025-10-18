package com.vilaagro.api.model;

/**
 * Enum que representa o tipo de usuário no sistema
 */
public enum UserType {
    ADMIN("Admin"),
    PRODUTOR_RURAL("Produtor Rural"),
    GASTRONOMO("Gastronomo"),
    PRODUTOR_ARTESANAL("Produtor Artesanal");

    private final String value;

    UserType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
