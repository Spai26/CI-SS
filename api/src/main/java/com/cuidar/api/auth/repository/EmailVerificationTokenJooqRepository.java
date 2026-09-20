package com.cuidar.api.auth.repository;

import com.cuidar.api.auth.repository.EmailVerificationTokenRepository.EmailVerificationToken;
import com.cuidar.api.jooq.generated.tables.records.EmailVerificationTokenRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * jOOQ-backed implementation of {@link EmailVerificationTokenRepository}.
 */
@Repository
public class EmailVerificationTokenJooqRepository implements EmailVerificationTokenRepository {

    private static final com.cuidar.api.jooq.generated.tables.EmailVerificationToken T =
            com.cuidar.api.jooq.generated.tables.EmailVerificationToken.EMAIL_VERIFICATION_TOKEN;

    private final DSLContext dsl;

    public EmailVerificationTokenJooqRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public EmailVerificationToken save(EmailVerificationToken token) {
        com.cuidar.api.jooq.generated.tables.records.EmailVerificationTokenRecord rec =
                dsl.newRecord(T);
        rec.setUsuarioId(token.usuarioId());
        rec.setToken(token.token());
        rec.setFechaExpiracion(token.fechaExpiracion());
        rec.store();
        return toDomain(rec);
    }

    @Override
    public Optional<EmailVerificationToken> findByToken(String token) {
        return dsl.selectFrom(T)
                .where(com.cuidar.api.jooq.generated.tables.EmailVerificationToken.EMAIL_VERIFICATION_TOKEN.TOKEN.eq(token))
                .fetchOptional()
                .map(this::toDomain);
    }

    @Override
    public void markUsed(EmailVerificationToken token) {
        dsl.update(com.cuidar.api.jooq.generated.tables.EmailVerificationToken.EMAIL_VERIFICATION_TOKEN)
                .set(com.cuidar.api.jooq.generated.tables.EmailVerificationToken.EMAIL_VERIFICATION_TOKEN.FECHA_USO,
                        java.time.OffsetDateTime.now())
                .where(com.cuidar.api.jooq.generated.tables.EmailVerificationToken.EMAIL_VERIFICATION_TOKEN.ID.eq(token.id()))
                .execute();
    }

    private EmailVerificationToken toDomain(EmailVerificationTokenRecord rec) {
        return new EmailVerificationToken(
                rec.getId(),
                rec.getUsuarioId(),
                rec.getToken(),
                rec.getFechaCreacion(),
                rec.getFechaExpiracion(),
                rec.getFechaUso()
        );
    }
}