package com.cuidar.api.auth.api.dto;

/**
 * Login response: access token + minimal public user info.
 *
 * <p>Refresh tokens (when added) will live in a separate endpoint.</p>
 */
public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds,
        UsuarioResponse usuario
) {
    public static final String BEARER = "Bearer";
}
