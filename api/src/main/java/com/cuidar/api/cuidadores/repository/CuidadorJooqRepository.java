package com.cuidar.api.cuidadores.repository;

import com.cuidar.api.cuidadores.api.dto.BusquedaCuidadorRequest;
import com.cuidar.api.cuidadores.domain.Cuidador;
import com.cuidar.api.jooq.generated.tables.records.CuidadorRecord;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CuidadorJooqRepository implements CuidadorRepository {

    private static final com.cuidar.api.jooq.generated.tables.Cuidador C =
            com.cuidar.api.jooq.generated.tables.Cuidador.CUIDADOR;

    private final DSLContext dsl;

    public CuidadorJooqRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public Optional<Cuidador> findById(Long id) {
        return dsl.selectFrom(C)
                .where(C.ID.eq(id))
                .fetchOptional()
                .map(this::toDomain);
    }

    @Override
    public Optional<Cuidador> findByUsuarioId(Long usuarioId) {
        return dsl.selectFrom(C)
                .where(C.USUARIO_ID.eq(usuarioId))
                .fetchOptional()
                .map(this::toDomain);
    }

    @Override
    public Cuidador insert(Cuidador cuidador) {
        CuidadorRecord rec = dsl.newRecord(C);
        rec.setUsuarioId(cuidador.usuarioId());
        rec.setPresentacion(cuidador.presentacion());
        rec.setAnosExperiencia(cuidador.anosExperiencia());
        rec.setTarifaReferencial(cuidador.tarifaReferencial());
        rec.setEstadoVerificacion(cuidador.estadoVerificacion());
        rec.setEstadoPublicacion(cuidador.estadoPublicacion());
        rec.setCalificacionPromedio(cuidador.calificacionPromedio());
        rec.store();
        return toDomain(rec);
    }

    @Override
    public Cuidador update(Cuidador cuidador) {
        CuidadorRecord rec = dsl.fetchOne(C, C.ID.eq(cuidador.id()));
        if (rec == null) {
            throw new IllegalStateException("Cuidador no encontrado: id=" + cuidador.id());
        }
        rec.setPresentacion(cuidador.presentacion());
        rec.setAnosExperiencia(cuidador.anosExperiencia());
        rec.setTarifaReferencial(cuidador.tarifaReferencial());
        rec.setEstadoVerificacion(cuidador.estadoVerificacion());
        rec.setEstadoPublicacion(cuidador.estadoPublicacion());
        rec.setCalificacionPromedio(cuidador.calificacionPromedio());
        rec.store();
        return toDomain(rec);
    }

    @Override
    public List<Cuidador> buscarCuidadores(BusquedaCuidadorRequest filtros) {
        Condition condition = DSL.noCondition()
                .and(C.ESTADO_PUBLICACION.eq(Cuidador.ESTADO_PUBLICACION_PUBLICADO))
                .and(C.ESTADO_VERIFICACION.eq(Cuidador.ESTADO_VERIFICACION_VERIFICADO));

        if (filtros.experienciaMin() != null) {
            condition = condition.and(C.ANOS_EXPERIENCIA.ge(filtros.experienciaMin()));
        }
        if (filtros.tarifaMax() != null) {
            condition = condition.and(C.TARIFA_REFERENCIAL.le(filtros.tarifaMax()));
        }
        if (filtros.calificacionMin() != null) {
            condition = condition.and(C.CALIFICACION_PROMEDIO.ge(filtros.calificacionMin()));
        }

        return dsl.selectFrom(C)
                .where(condition)
                .orderBy(C.CALIFICACION_PROMEDIO.desc().nullsLast())
                .fetch()
                .map(this::toDomain);
    }

    private Cuidador toDomain(CuidadorRecord rec) {
        return new Cuidador(
                rec.getId(),
                rec.getUsuarioId(),
                rec.getPresentacion(),
                rec.getAnosExperiencia(),
                rec.getTarifaReferencial(),
                rec.getEstadoVerificacion(),
                rec.getEstadoPublicacion(),
                rec.getCalificacionPromedio(),
                rec.getCreatedAt(),
                rec.getUpdatedAt()
        );
    }
}
