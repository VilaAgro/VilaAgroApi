package com.vilaagro.api.model;

/**
 * Tipos de ausência
 */
public enum AbsenceType {
    /**
     * Ausência notificada com antecedência pelo comerciante
     * O usuário avisou antes que não compareceria
     */
    NOTIFIED,
    
    /**
     * Ausência registrada pelo admin após o fato
     * O usuário faltou sem avisar e o admin registrou a falta
     */
    REGISTERED
}
