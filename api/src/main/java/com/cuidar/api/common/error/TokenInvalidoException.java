package com.cuidar.api.common.error;

/**
 * Thrown when an email-verification token is invalid (not found, expired, or
 * already used).
 *
 * <p>Handled by {@link GlobalExceptionHandler} as HTTP 400.</p>
 */
public class TokenInvalidoException extends RuntimeException {

    public TokenInvalidoException(String token) {
        super("Token de verificación inválido o expirado");
    }
}