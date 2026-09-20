package com.cuidar.api.common.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT/OAuth2 configuration bound to {@code app.security.jwt.*}.
 *
 * <p>{@code issuerUri} is the URL of the external identity provider
 * (Keycloak realm, Auth0 tenant, etc.) used to fetch the JWKS.</p>
 */
@ConfigurationProperties(prefix = "app.security.jwt")
public record JwtProperties(String issuerUri) {
}