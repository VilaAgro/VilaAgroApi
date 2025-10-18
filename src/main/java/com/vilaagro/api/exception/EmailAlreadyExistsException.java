package com.vilaagro.api.exception;

/**
 * Exceção lançada quando há tentativa de cadastrar email duplicado
 */
public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String email) {
        super(String.format("Email '%s' já está em uso", email));
    }
}
