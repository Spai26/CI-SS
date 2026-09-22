package com.cuidar.api.reservas.repository;

import com.cuidar.api.reservas.domain.Reserva;
import com.cuidar.api.jooq.generated.tables.records.ReservaRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ReservaJooqRepository implements ReservaRepository {

    private static final com.cuidar.api.jooq.generated.tables.Reserva R =
            com.cuidar.api.jooq.generated.tables.Reserva.RESERVA;

    private final DSLContext dsl;

    public ReservaJooqRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public Optional<Reserva> findById(Long id) {
        return dsl.selectFrom(R)
                .where(R.ID.eq(id))
                .fetchOptional()
                .map(this::toDomain);
    }

    @Override
    public List<Reserva> findByFamiliaId(Long familiaId) {
        return dsl.selectFrom(R)
                .where(R.FAMILIA_ID.eq(familiaId))
                .orderBy(R.CREATED_AT.desc())
                .fetch()
                .map(this::toDomain);
    }

    @Override
    public List<Reserva> findByCuidadorId(Long cuidadorId) {
        return dsl.selectFrom(R)
                .where(R.CUIDADOR_ID.eq(cuidadorId))
                .orderBy(R.CREATED_AT.desc())
                .fetch()
                .map(this::toDomain);
    }

    @Override
    public Reserva insert(Reserva reserva) {
        ReservaRecord rec = dsl.newRecord(R);
        rec.setFamiliaId(reserva.familiaId());
        rec.setCuidadorId(reserva.cuidadorId());
        rec.setAdultoMayorId(reserva.adultoMayorId());
        rec.setFechaInicio(reserva.fechaInicio());
        rec.setFechaFin(reserva.fechaFin());
        rec.setModalidad(reserva.modalidad());
        rec.setHorasEstimadas(reserva.horasEstimadas());
        rec.setMontoTotal(reserva.montoTotal());
        rec.setEstado(reserva.estado());
        rec.setMotivoCancelacion(reserva.motivoCancelacion());
        rec.store();
        return toDomain(rec);
    }

    @Override
    public Reserva update(Reserva reserva) {
        ReservaRecord rec = dsl.fetchOne(R, R.ID.eq(reserva.id()));
        if (rec == null) {
            throw new IllegalStateException("Reserva no encontrada: " + reserva.id());
        }
        rec.setFechaInicio(reserva.fechaInicio());
        rec.setFechaFin(reserva.fechaFin());
        rec.setModalidad(reserva.modalidad());
        rec.setHorasEstimadas(reserva.horasEstimadas());
        rec.setMontoTotal(reserva.montoTotal());
        rec.setEstado(reserva.estado());
        rec.setMotivoCancelacion(reserva.motivoCancelacion());
        rec.store();
        return toDomain(rec);
    }

    private Reserva toDomain(ReservaRecord rec) {
        return new Reserva(
                rec.getId(),
                rec.getFamiliaId(),
                rec.getCuidadorId(),
                rec.getAdultoMayorId(),
                rec.getFechaInicio(),
                rec.getFechaFin(),
                rec.getModalidad(),
                rec.getHorasEstimadas(),
                rec.getMontoTotal(),
                rec.getEstado(),
                rec.getMotivoCancelacion(),
                rec.getCreatedAt(),
                rec.getUpdatedAt()
        );
    }
}
