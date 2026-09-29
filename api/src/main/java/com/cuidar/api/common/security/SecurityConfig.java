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
 * Configuración global de seguridad de la aplicación.
 *
 * <p>Define las políticas de acceso para las rutas públicas (como el login o actuator) 
 * y asegura que todos los endpoints bajo {@code /api/**} requieran autenticación JWT válida.</p>
 *
 * <p>El decodificador de tokens ({@link JwtDecoder}) se inyecta según el perfil activo:</p>
 * <ul>
 *   <li>{@code prod}: Valida firmas y claves remotas contra el Identity Provider (IdP) real usando JWKS.</li>
 *   <li>{@code dev}: Decodificador permisivo local que usa una llave simétrica compartida para facilitar las pruebas sin IdP.</li>
 *   <li>{@code test}: La seguridad web se deshabilita para las pruebas de integración unitarias.</li>
 * </ul>
 *
 * @author Equipo de Arquitectura y Seguridad CI-SS 
 * @version 1.1.0
 * @see org.springframework.security.config.annotation.web.builders.HttpSecurity
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
        String DEV_SECRET = "dev-secret-dev-secret-dev-secret-dev-secret-dev-secret";
        javax.crypto.SecretKey key = new javax.crypto.spec.SecretKeySpec(DEV_SECRET.getBytes(), "HmacSHA256");
        return org.springframework.security.oauth2.jwt.NimbusJwtDecoder.withSecretKey(key).build();
    }
}