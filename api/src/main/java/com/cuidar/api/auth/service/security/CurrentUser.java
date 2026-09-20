package com.cuidar.api.auth.service.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.cuidar.api.common.error.CredencialesInvalidasException;

/**
 * Helper for endpoints/controllers to read the authenticated user id from
 * the current {@code JwtAuthenticationToken} (sub claim). Throws if there is
 * no JWT in the security context.
 */
@Component
public class CurrentUser {

    /**
     * @return the id of the authenticated user (the JWT {@code sub} claim)
     * @throws com.cuidar.api.common.error.CredencialesInvalidasException if
     *         no JWT is present
     */
    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            throw new CredencialesInvalidasException();
        }
        try {
            return Long.parseLong(jwt.getSubject());
        } catch (NumberFormatException e) {
            throw new CredencialesInvalidasException();
        }
    }
}