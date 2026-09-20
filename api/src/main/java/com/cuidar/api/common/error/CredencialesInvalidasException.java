package com.cuidar.api.common.error;

/**
 * Thrown when authentication fails (wrong email/password or inactive user).
 *
 * <p>The handler always returns the same generic message so that callers
 * cannot enumerate which emails are registered.</p>
 *
 * <p>Handled by {@link GlobalExceptionHandler} as HTTP 401.</p>
 */
public class CredencialesInvalidasException extends RuntimeException {

    public CredencialesInvalidasException() {
        super("Credenciales inválidas");
    }
}