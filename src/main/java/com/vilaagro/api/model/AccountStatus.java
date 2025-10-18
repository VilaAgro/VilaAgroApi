package com.vilaagro.api.model;

/**
 * Enum que representa o status da conta do usuário
 */
public enum AccountStatus {
    PENDING("Pending"),
    APPROVED("Approved"),
    ACTIVE("Active"),
    DISAPPROVED("Disapproved"),
    INACTIVE("Inactive");

    private final String value;

    AccountStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
