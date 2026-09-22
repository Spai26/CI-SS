package com.cuidar.api.familias.repository;

import com.cuidar.api.familias.domain.Familia;
import com.cuidar.api.jooq.generated.tables.records.FamiliaRecord;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class FamiliaJooqRepository implements FamiliaRepository {

    private static final com.cuidar.api.jooq.generated.tables.Familia F =
            com.cuidar.api.jooq.generated.tables.Familia.FAMILIA;

    private final DSLContext dsl;

    public FamiliaJooqRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public Optional<Familia> findById(Long id) {
        return dsl.selectFrom(F)
                .where(F.ID.eq(id))
                .fetchOptional()
                .map(this::toDomain);
    }

    @Override
    public Optional<Familia> findByUsuarioId(Long usuarioId) {
        return dsl.selectFrom(F)
                .where(F.USUARIO_ID.eq(usuarioId))
                .fetchOptional()
                .map(this::toDomain);
    }

    @Override
    public Familia insert(Familia familia) {
        FamiliaRecord rec = dsl.newRecord(F);
        rec.setUsuarioId(familia.usuarioId());
        rec.setDireccion(familia.direccion());
        if (familia.preferenciasBusqueda() != null) {
            rec.setPreferenciasBusqueda(JSONB.valueOf(familia.preferenciasBusqueda()));
        }
        rec.store();
        return toDomain(rec);
    }

    @Override
    public Familia update(Familia familia) {
        FamiliaRecord rec = dsl.fetchOne(F, F.ID.eq(familia.id()));
        if (rec == null) {
            throw new IllegalStateException("Familia no encontrada: id=" + familia.id());
        }
        rec.setDireccion(familia.direccion());
        if (familia.preferenciasBusqueda() != null) {
            rec.setPreferenciasBusqueda(JSONB.valueOf(familia.preferenciasBusqueda()));
        } else {
            rec.setPreferenciasBusqueda(null);
        }
        rec.store();
        return toDomain(rec);
    }

    private Familia toDomain(FamiliaRecord rec) {
        return new Familia(
                rec.getId(),
                rec.getUsuarioId(),
                rec.getDireccion(),
                rec.getPreferenciasBusqueda() != null ? rec.getPreferenciasBusqueda().data() : null,
                rec.getCreatedAt(),
                rec.getUpdatedAt()
        );
    }
}
