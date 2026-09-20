package com.cuidar.api.auth.service.security;

import com.cuidar.api.auth.domain.Usuario;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

/**
 * HMAC-SHA256 JWT issuer backed by Nimbus (already on the classpath via
 * Spring Security's OAuth2 Resource Server).
 *
 * <p>For dev/test we sign with a static secret. In prod this MUST come from
 * a secret manager / env var and rotate regularly.</p>
 */
@Service
public class HmacJwtService implements JwtService {

    /** Secret key length must be at least 256 bits for HS256. */
    private static final String DEV_SECRET =
            "dev-secret-dev-secret-dev-secret-dev-secret-dev-secret";

    private static final long EXPIRES_IN_SECONDS = 3600L;
    private static final String ISSUER = "cuidar-api";

    private final byte[] secretBytes = DEV_SECRET.getBytes();

    @Override
    public String generarToken(Usuario usuario) {
        try {
            Instant now = Instant.now();
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .issuer(ISSUER)
                    .subject(String.valueOf(usuario.id()))
                    .claim("email", usuario.email())
                    .claim("nombre", usuario.nombre())
                    .claim("apellido", usuario.apellido())
                    .claim("estado", usuario.estado())
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(now.plusSeconds(EXPIRES_IN_SECONDS)))
                    .build();

            SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
            jwt.sign(new MACSigner(secretBytes));
            return jwt.serialize();
        } catch (JOSEException e) {
            throw new IllegalStateException("No se pudo firmar el JWT", e);
        }
    }

    @Override
    public long expiresInSeconds() {
        return EXPIRES_IN_SECONDS;
    }
}