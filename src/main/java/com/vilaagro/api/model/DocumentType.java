package com.vilaagro.api.model;

/**
 * Enum para tipos de documentos
 */
public enum DocumentType {
    RG,        // Documento de identidade
    CPF,       // Cadastro de Pessoa Física
    CAF,       // Cadastro de Agricultor Familiar (para PRODUTOR_RURAL)
    CNPJ,      // Cadastro Nacional de Pessoa Jurídica (para GASTRONOMO)
    OTHER      // Outros documentos
}
