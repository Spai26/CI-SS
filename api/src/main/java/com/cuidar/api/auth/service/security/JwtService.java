package com.cuidar.api.auth.service.security;

import com.cuidar.api.auth.domain.Usuario;

/**
 * Abstraction over JWT issuing so the service layer does not depend on
 * a particular JWT library implementation.
 */
public interface JwtService {

    /**
     * Issues a signed JWT for the given user.
     *
     * @param usuario the user to embed as the {@code sub} claim
     * @return the encoded JWT string
     */
    String generarToken(Usuario usuario);

    /**
     * @return the token lifetime, in seconds
     */
    long expiresInSeconds();
}