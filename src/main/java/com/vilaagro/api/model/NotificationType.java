package com.vilaagro.api.model;

/**
 * Enum para tipos de notificação
 */
public enum NotificationType {
    GENERAL("general"),
    ABSENCE("absence"),
    COURSE("course"),
    APPROVAL("approval"),
    FAIR("fair"),
    SYSTEM("system");

    private final String value;

    NotificationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
