package com.cuidar.api.familias.repository;

import com.cuidar.api.familias.domain.AdultoMayor;
import com.cuidar.api.familias.domain.GeneroAdulto;
import com.cuidar.api.jooq.generated.tables.records.AdultoMayorRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AdultoMayorJooqRepository implements AdultoMayorRepository {

    private static final com.cuidar.api.jooq.generated.tables.AdultoMayor A =
            com.cuidar.api.jooq.generated.tables.AdultoMayor.ADULTO_MAYOR;

    private final DSLContext dsl;

    public AdultoMayorJooqRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public Optional<AdultoMayor> findById(Long id) {
        return dsl.selectFrom(A)
                .where(A.ID.eq(id))
                .fetchOptional()
                .map(this::toDomain);
    }

    @Override
    public List<AdultoMayor> findByFamiliaId(Long familiaId) {
        return dsl.selectFrom(A)
                .where(A.FAMILIA_ID.eq(familiaId))
                .orderBy(A.CREATED_AT.desc())
                .fetch()
                .map(this::toDomain);
    }

    @Override
    public AdultoMayor insert(AdultoMayor adultoMayor) {
        AdultoMayorRecord rec = dsl.newRecord(A);
        rec.setFamiliaId(adultoMayor.familiaId());
        rec.setNombre(adultoMayor.nombre());
        rec.setApellido(adultoMayor.apellido());
        rec.setFechaNacimiento(adultoMayor.fechaNacimiento());
        if (adultoMayor.genero() != null) {
            rec.setGenero(adultoMayor.genero().name());
        }
        rec.setCondicionSalud(adultoMayor.condicionSalud());
        rec.setNotas(adultoMayor.notas());
        rec.store();
        return toDomain(rec);
    }

    @Override
    public AdultoMayor update(AdultoMayor adultoMayor) {
        AdultoMayorRecord rec = dsl.fetchOne(A, A.ID.eq(adultoMayor.id()));
        if (rec == null) {
            throw new IllegalStateException("AdultoMayor no encontrado: id=" + adultoMayor.id());
        }
        rec.setNombre(adultoMayor.nombre());
        rec.setApellido(adultoMayor.apellido());
        rec.setFechaNacimiento(adultoMayor.fechaNacimiento());
        if (adultoMayor.genero() != null) {
            rec.setGenero(adultoMayor.genero().name());
        } else {
            rec.setGenero(null);
        }
        rec.setCondicionSalud(adultoMayor.condicionSalud());
        rec.setNotas(adultoMayor.notas());
        rec.store();
        return toDomain(rec);
    }

    @Override
    public void deleteById(Long id) {
        dsl.deleteFrom(A)
                .where(A.ID.eq(id))
                .execute();
    }

    private AdultoMayor toDomain(AdultoMayorRecord rec) {
        return new AdultoMayor(
                rec.getId(),
                rec.getFamiliaId(),
                rec.getNombre(),
                rec.getApellido(),
                rec.getFechaNacimiento(),
                rec.getGenero() != null ? GeneroAdulto.valueOf(rec.getGenero()) : null,
                rec.getCondicionSalud(),
                rec.getNotas(),
                rec.getCreatedAt(),
                rec.getUpdatedAt()
        );
    }
}
