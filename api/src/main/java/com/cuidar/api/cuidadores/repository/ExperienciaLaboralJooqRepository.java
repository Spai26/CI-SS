package com.cuidar.api.cuidadores.repository;

import com.cuidar.api.cuidadores.domain.ExperienciaLaboral;
import com.cuidar.api.jooq.generated.tables.records.ExperienciaLaboralRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ExperienciaLaboralJooqRepository implements ExperienciaLaboralRepository {

    private static final com.cuidar.api.jooq.generated.tables.ExperienciaLaboral E =
            com.cuidar.api.jooq.generated.tables.ExperienciaLaboral.EXPERIENCIA_LABORAL;

    private final DSLContext dsl;

    public ExperienciaLaboralJooqRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public Optional<ExperienciaLaboral> findById(Long id) {
        return dsl.selectFrom(E)
                .where(E.ID.eq(id))
                .fetchOptional()
                .map(this::toDomain);
    }

    @Override
    public List<ExperienciaLaboral> findByCuidadorId(Long cuidadorId) {
        return dsl.selectFrom(E)
                .where(E.CUIDADOR_ID.eq(cuidadorId))
                .orderBy(E.FECHA_INICIO.desc())
                .fetch()
                .map(this::toDomain);
    }

    @Override
    public ExperienciaLaboral insert(ExperienciaLaboral experiencia) {
        ExperienciaLaboralRecord rec = dsl.newRecord(E);
        rec.setCuidadorId(experiencia.cuidadorId());
        rec.setEmpresa(experiencia.empresa());
        rec.setCargo(experiencia.cargo());
        rec.setFechaInicio(experiencia.fechaInicio());
        rec.setFechaFin(experiencia.fechaFin());
        rec.setDescripcion(experiencia.descripcion());
        rec.store();
        return toDomain(rec);
    }

    @Override
    public void deleteById(Long id) {
        dsl.deleteFrom(E)
                .where(E.ID.eq(id))
                .execute();
    }

    private ExperienciaLaboral toDomain(ExperienciaLaboralRecord rec) {
        return new ExperienciaLaboral(
                rec.getId(),
                rec.getCuidadorId(),
                rec.getEmpresa(),
                rec.getCargo(),
                rec.getFechaInicio(),
                rec.getFechaFin(),
                rec.getDescripcion(),
                rec.getCreatedAt(),
                rec.getUpdatedAt()
        );
    }
}
