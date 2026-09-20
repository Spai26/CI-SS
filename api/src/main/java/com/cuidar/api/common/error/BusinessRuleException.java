package com.cuidar.api.common.error;

/**
 * Thrown when a domain or business rule is violated by the caller.
 *
 * <p>Handled by {@link GlobalExceptionHandler} as HTTP 422 Unprocessable Entity.</p>
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}