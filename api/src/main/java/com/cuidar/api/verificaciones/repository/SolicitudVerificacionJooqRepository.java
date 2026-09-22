package com.cuidar.api.verificaciones.repository;

import com.cuidar.api.jooq.generated.tables.records.DocumentoVerificacionRecord;
import com.cuidar.api.jooq.generated.tables.records.SolicitudVerificacionRecord;
import com.cuidar.api.verificaciones.domain.DocumentoVerificacion;
import com.cuidar.api.verificaciones.domain.SolicitudVerificacion;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SolicitudVerificacionJooqRepository implements SolicitudVerificacionRepository {

    private static final com.cuidar.api.jooq.generated.tables.SolicitudVerificacion S =
            com.cuidar.api.jooq.generated.tables.SolicitudVerificacion.SOLICITUD_VERIFICACION;

    private static final com.cuidar.api.jooq.generated.tables.DocumentoVerificacion D =
            com.cuidar.api.jooq.generated.tables.DocumentoVerificacion.DOCUMENTO_VERIFICACION;

    private final DSLContext dsl;

    public SolicitudVerificacionJooqRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public SolicitudVerificacion insert(SolicitudVerificacion solicitud) {
        SolicitudVerificacionRecord rec = dsl.newRecord(S);
        rec.setCuidadorId(solicitud.cuidadorId());
        rec.setFechaSolicitud(solicitud.fechaSolicitud());
        rec.setEstado(solicitud.estado());
        rec.setObservaciones(solicitud.observaciones());
        rec.store();

        Long newId = rec.getId();

        if (solicitud.documentos() != null) {
            for (DocumentoVerificacion doc : solicitud.documentos()) {
                DocumentoVerificacionRecord dRec = dsl.newRecord(D);
                dRec.setSolicitudId(newId);
                dRec.setTipoDocumento(doc.tipoDocumento());
                dRec.setArchivoUrl(doc.archivoUrl());
                dRec.setEstado(doc.estado());
                dRec.store();
            }
        }

        return findById(newId).orElseThrow();
    }

    @Override
    public SolicitudVerificacion update(SolicitudVerificacion solicitud) {
        SolicitudVerificacionRecord rec = dsl.fetchOne(S, S.ID.eq(solicitud.id()));
        if (rec != null) {
            rec.setFechaResolucion(solicitud.fechaResolucion());
            rec.setEstado(solicitud.estado());
            rec.setObservaciones(solicitud.observaciones());
            rec.setRevisadaPor(solicitud.revisadaPor());
            rec.store();
        }
        return findById(solicitud.id()).orElseThrow();
    }

    @Override
    public Optional<SolicitudVerificacion> findById(Long id) {
        SolicitudVerificacionRecord rec = dsl.selectFrom(S)
                .where(S.ID.eq(id))
                .fetchOne();
        if (rec == null) return Optional.empty();

        List<DocumentoVerificacion> docs = getDocumentos(rec.getId());
        return Optional.of(toDomain(rec, docs));
    }

    @Override
    public List<SolicitudVerificacion> findAllPendientes() {
        return dsl.selectFrom(S)
                .where(S.ESTADO.eq(SolicitudVerificacion.ESTADO_PENDIENTE))
                .orderBy(S.FECHA_SOLICITUD.asc())
                .fetch()
                .map(rec -> toDomain(rec, getDocumentos(rec.getId())));
    }

    @Override
    public Optional<SolicitudVerificacion> findLatestByCuidadorId(Long cuidadorId) {
        SolicitudVerificacionRecord rec = dsl.selectFrom(S)
                .where(S.CUIDADOR_ID.eq(cuidadorId))
                .orderBy(S.FECHA_SOLICITUD.desc())
                .limit(1)
                .fetchOne();
        if (rec == null) return Optional.empty();

        List<DocumentoVerificacion> docs = getDocumentos(rec.getId());
        return Optional.of(toDomain(rec, docs));
    }

    private List<DocumentoVerificacion> getDocumentos(Long solicitudId) {
        return dsl.selectFrom(D)
                .where(D.SOLICITUD_ID.eq(solicitudId))
                .fetch()
                .map(this::toDocDomain);
    }

    private SolicitudVerificacion toDomain(SolicitudVerificacionRecord rec, List<DocumentoVerificacion> docs) {
        return new SolicitudVerificacion(
                rec.getId(),
                rec.getCuidadorId(),
                rec.getFechaSolicitud(),
                rec.getFechaResolucion(),
                rec.getEstado(),
                rec.getObservaciones(),
                rec.getRevisadaPor(),
                rec.getCreatedAt(),
                rec.getUpdatedAt(),
                docs
        );
    }

    private DocumentoVerificacion toDocDomain(DocumentoVerificacionRecord rec) {
        return new DocumentoVerificacion(
                rec.getId(),
                rec.getSolicitudId(),
                rec.getTipoDocumento(),
                rec.getArchivoUrl(),
                rec.getEstado(),
                rec.getObservaciones(),
                rec.getCreatedAt(),
                rec.getUpdatedAt()
        );
    }
}
