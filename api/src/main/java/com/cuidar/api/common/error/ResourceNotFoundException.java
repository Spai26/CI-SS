package com.cuidar.api.common.error;

/**
 * Thrown when a referenced resource (by id, slug, etc.) does not exist.
 *
 * <p>Handled by {@link GlobalExceptionHandler} as HTTP 404.</p>
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException of(String resource, Object id) {
        return new ResourceNotFoundException("%s with id %s not found".formatted(resource, id));
    }
}