package com.cuidar.api.auth.repository;

import com.cuidar.api.auth.domain.Usuario;
import com.cuidar.api.jooq.generated.tables.records.UsuarioRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * jOOQ-backed implementation of {@link UsuarioRepository}.
 *
 * <p>This is the ONLY place in the codebase that knows that {@code Usuario}
 * is persisted in the {@code usuario} table. It translates to and from the
 * generated {@code UsuarioRecord}; the service layer stays untouched.</p>
 */
@Repository
public class UsuarioJooqRepository implements UsuarioRepository {

    private static final com.cuidar.api.jooq.generated.tables.Usuario U =
            com.cuidar.api.jooq.generated.tables.Usuario.USUARIO;

    private final DSLContext dsl;

    public UsuarioJooqRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public boolean existsByEmail(String email) {
        return dsl.fetchExists(
                dsl.selectFrom(U).where(U.EMAIL.eq(email))
        );
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return dsl.selectFrom(U)
                .where(U.EMAIL.eq(email))
                .fetchOptional()
                .map(this::toDomain);
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return dsl.selectFrom(U)
                .where(U.ID.eq(id))
                .fetchOptional()
                .map(this::toDomain);
    }

    @Override
    public Usuario insert(Usuario usuario) {
        UsuarioRecord rec = dsl.newRecord(U);
        rec.setEmail(usuario.email());
        rec.setPasswordHash(usuario.passwordHash());
        rec.setNombre(usuario.nombre());
        rec.setApellido(usuario.apellido());
        rec.setTelefono(usuario.telefono());
        rec.setEstado(usuario.estado());
        rec.setEmailVerificado(usuario.emailVerificado());
        rec.store();
        return toDomain(rec);
    }

    @Override
    public Usuario update(Usuario usuario) {
        UsuarioRecord rec = dsl.fetchOne(U, U.ID.eq(usuario.id()));
        if (rec == null) {
            throw new IllegalStateException("Usuario no encontrado: id=" + usuario.id());
        }
        rec.setNombre(usuario.nombre());
        rec.setApellido(usuario.apellido());
        rec.setTelefono(usuario.telefono());
        rec.setEstado(usuario.estado());
        rec.setEmailVerificado(usuario.emailVerificado());
        rec.store();
        return toDomain(rec);
    }

    private Usuario toDomain(UsuarioRecord rec) {
        return new Usuario(
                rec.getId(),
                rec.getEmail(),
                rec.getPasswordHash(),
                rec.getNombre(),
                rec.getApellido(),
                rec.getTelefono(),
                rec.getEstado(),
                rec.getEmailVerificado(),
                rec.getFechaRegistro()
        );
    }
}