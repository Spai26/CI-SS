package com.cuidar.api.auth.api.dto;

/**
 * Public representation of a {@code Usuario} for API responses.
 *
 * <p>Never includes the password hash or any other secret.</p>
 */
public record UsuarioResponse(
        Long id,
        String email,
        String nombre,
        String apellido,
        String telefono,
        String estado,
        boolean emailVerificado
) {
}