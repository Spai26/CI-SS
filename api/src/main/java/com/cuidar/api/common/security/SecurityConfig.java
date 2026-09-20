package com.cuidar.api.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.SecurityFilterChain;

import static com.cuidar.api.common.web.ApiPaths.AUTH;
import static com.cuidar.api.common.web.ApiPaths.V1;

/**
 * Security configuration.
 *
 * <p>Public routes: actuator health/info. Everything under {@code /api/**} requires a JWT.</p>
 *
 * <p>The {@link JwtDecoder} is wired by profile:</p>
 * <ul>
 *   <li>{@code prod}: real decoder backed by the external IdP issuer URI (JWKS is fetched).</li>
 *   <li>{@code dev}: a permissive decoder that accepts any token, so the API boots without a real IdP.
 *       Replace with the real decoder before any integration testing.</li>
 *   <li>{@code test}: security is disabled entirely (see {@code TestSecurityConfig}).</li>
 * </ul>
 */
@Configuration
public class SecurityConfig {

    @Bean
    @Profile("!test")
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, AUTH + "/registro").permitAll()
                        .requestMatchers(HttpMethod.POST, AUTH + "/login").permitAll()
                        .requestMatchers(V1 + "/**").authenticated()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> { /* JwtDecoder bean supplies the decoder */ }));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Real JwtDecoder for prod - fetches JWKS from the configured issuer.
     */
    @Bean
    @Profile("prod")
    public JwtDecoder jwtDecoderProd(JwtProperties props) {
        return JwtDecoders.fromIssuerLocation(props.issuerUri());
    }

    /**
     * Dev-only decoder that accepts any token without signature verification.
     * <p>WARNING: never use this in any environment where real data flows.</p>
     */
    @Bean
    @Profile("dev")
    public JwtDecoder jwtDecoderDev() {
        return token -> {
            var now = java.time.Instant.now();
            return org.springframework.security.oauth2.jwt.Jwt.withTokenValue(token)
                    .header("alg", "none")
                    .claim("sub", "dev-user")
                    .issuedAt(now)
                    .expiresAt(now.plusSeconds(3600))
                    .build();
        };
    }
}