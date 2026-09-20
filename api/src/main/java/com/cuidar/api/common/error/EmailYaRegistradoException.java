package com.cuidar.api.common.error;

/**
 * Thrown when attempting to register with an email that already exists in the system.
 *
 * <p>Handled by {@link GlobalExceptionHandler} as HTTP 409 Conflict.</p>
 */
public class EmailYaRegistradoException extends RuntimeException {

    public EmailYaRegistradoException(String email) {
        super("El email %s ya está registrado".formatted(email));
    }
}