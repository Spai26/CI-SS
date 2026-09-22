package com.cuidar.api.reportes.repository;

import com.cuidar.api.jooq.generated.tables.records.ReporteDiarioRecord;
import com.cuidar.api.reportes.domain.ReporteDiario;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReporteDiarioJooqRepository implements ReporteDiarioRepository {

    private static final com.cuidar.api.jooq.generated.tables.ReporteDiario R =
            com.cuidar.api.jooq.generated.tables.ReporteDiario.REPORTE_DIARIO;

    private final DSLContext dsl;

    public ReporteDiarioJooqRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public ReporteDiario insert(ReporteDiario reporte) {
        ReporteDiarioRecord rec = dsl.newRecord(R);
        rec.setReservaId(reporte.reservaId());
        rec.setFecha(reporte.fecha());
        rec.setHoraInicio(reporte.horaInicio());
        rec.setHoraFin(reporte.horaFin());
        rec.setActividades(reporte.actividades());
        rec.setObservaciones(reporte.observaciones());
        rec.setEstadoAnimo(reporte.estadoAnimo());
        rec.store();
        return toDomain(rec);
    }

    @Override
    public List<ReporteDiario> findByReservaId(Long reservaId) {
        return dsl.selectFrom(R)
                .where(R.RESERVA_ID.eq(reservaId))
                .orderBy(R.FECHA.desc(), R.HORA_INICIO.desc())
                .fetch()
                .map(this::toDomain);
    }

    private ReporteDiario toDomain(ReporteDiarioRecord rec) {
        return new ReporteDiario(
                rec.getId(),
                rec.getReservaId(),
                rec.getFecha(),
                rec.getHoraInicio(),
                rec.getHoraFin(),
                rec.getActividades(),
                rec.getObservaciones(),
                rec.getEstadoAnimo(),
                rec.getCreatedAt(),
                rec.getUpdatedAt()
        );
    }
}
