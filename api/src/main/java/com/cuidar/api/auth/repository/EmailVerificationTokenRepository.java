package com.cuidar.api.auth.repository;

import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * DAO contract for email-verification tokens (RF03).
 */
public interface EmailVerificationTokenRepository {

    /**
     * Persists a new token for the given user and returns the stored representation
     * (id + fechaCreacion populated by the DB).
     */
    EmailVerificationToken save(EmailVerificationToken token);

    Optional<EmailVerificationToken> findByToken(String token);

    /**
     * Marks the token as used (set fechaUso=now).
     */
    void markUsed(EmailVerificationToken token);

    /**
     * Domain token. Embedded as a public record for direct construction from
     * the service / repo layer.
     */
    record EmailVerificationToken(
            Long id,
            Long usuarioId,
            String token,
            OffsetDateTime fechaCreacion,
            OffsetDateTime fechaExpiracion,
            OffsetDateTime fechaUso
    ) {
        public boolean isExpired(OffsetDateTime now) {
            return fechaExpiracion != null && fechaExpiracion.isBefore(now);
        }

        public boolean isUsed() {
            return fechaUso != null;
        }
    }
}