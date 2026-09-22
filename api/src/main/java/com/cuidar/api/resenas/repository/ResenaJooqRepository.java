package com.cuidar.api.resenas.repository;

import com.cuidar.api.jooq.generated.tables.records.ResenaRecord;
import com.cuidar.api.resenas.domain.Resena;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class ResenaJooqRepository implements ResenaRepository {

    private static final com.cuidar.api.jooq.generated.tables.Resena R =
            com.cuidar.api.jooq.generated.tables.Resena.RESENA;

    private final DSLContext dsl;

    public ResenaJooqRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public Resena insert(Resena resena) {
        ResenaRecord rec = dsl.newRecord(R);
        rec.setReservaId(resena.reservaId());
        rec.setAutorId(resena.autorId());
        rec.setDestinatarioId(resena.destinatarioId());
        rec.setCalificacion(resena.calificacion());
        rec.setComentario(resena.comentario());
        rec.setFecha(resena.fecha());
        rec.store();
        return toDomain(rec);
    }

    @Override
    public List<Resena> findByDestinatarioId(Long destinatarioId) {
        return dsl.selectFrom(R)
                .where(R.DESTINATARIO_ID.eq(destinatarioId))
                .orderBy(R.FECHA.desc())
                .fetch()
                .map(this::toDomain);
    }

    @Override
    public BigDecimal calcularPromedioPorDestinatario(Long destinatarioId) {
        BigDecimal avg = dsl.select(DSL.avg(R.CALIFICACION))
                .from(R)
                .where(R.DESTINATARIO_ID.eq(destinatarioId))
                .fetchOneInto(BigDecimal.class);
        
        return avg == null ? BigDecimal.ZERO : avg;
    }

    private Resena toDomain(ResenaRecord rec) {
        return new Resena(
                rec.getId(),
                rec.getReservaId(),
                rec.getAutorId(),
                rec.getDestinatarioId(),
                rec.getCalificacion(),
                rec.getComentario(),
                rec.getFecha(),
                rec.getCreatedAt(),
                rec.getUpdatedAt()
        );
    }
}
