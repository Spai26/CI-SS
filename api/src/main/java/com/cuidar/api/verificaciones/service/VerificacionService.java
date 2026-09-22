package com.cuidar.api.verificaciones.service;

import com.cuidar.api.cuidadores.domain.Cuidador;
import com.cuidar.api.cuidadores.repository.CuidadorRepository;
import com.cuidar.api.verificaciones.api.dto.CrearSolicitudRequest;
import com.cuidar.api.verificaciones.api.dto.DocumentoVerificacionRequest;
import com.cuidar.api.verificaciones.api.dto.ResolverSolicitudRequest;
import com.cuidar.api.verificaciones.domain.DocumentoVerificacion;
import com.cuidar.api.verificaciones.domain.SolicitudVerificacion;
import com.cuidar.api.verificaciones.repository.SolicitudVerificacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class VerificacionService {

    private final SolicitudVerificacionRepository solicitudRepository;
    private final CuidadorRepository cuidadorRepository;

    public VerificacionService(SolicitudVerificacionRepository solicitudRepository, CuidadorRepository cuidadorRepository) {
        this.solicitudRepository = solicitudRepository;
        this.cuidadorRepository = cuidadorRepository;
    }

    @Transactional
    public SolicitudVerificacion crearSolicitud(Long cuidadorUsuarioId, CrearSolicitudRequest req) {
        Cuidador cuidador = cuidadorRepository.findByUsuarioId(cuidadorUsuarioId)
                .orElseThrow(() -> new IllegalArgumentException("El cuidador no existe"));

        // Verificar si ya tiene una pendiente
        solicitudRepository.findLatestByCuidadorId(cuidador.id()).ifPresent(s -> {
            if (s.estado().equals(SolicitudVerificacion.ESTADO_PENDIENTE)) {
                throw new IllegalStateException("Ya existe una solicitud pendiente de verificación");
            }
        });

        List<DocumentoVerificacion> docs = req.documentos().stream()
                .map(dReq -> new DocumentoVerificacion(null, null, dReq.tipoDocumento(), dReq.archivoUrl(), SolicitudVerificacion.ESTADO_PENDIENTE, null, null, null))
                .toList();

        SolicitudVerificacion nuevaSolicitud = new SolicitudVerificacion(
                null,
                cuidador.id(),
                OffsetDateTime.now(),
                null,
                SolicitudVerificacion.ESTADO_PENDIENTE,
                null,
                null,
                null,
                null,
                docs
        );

        return solicitudRepository.insert(nuevaSolicitud);
    }

    @Transactional(readOnly = true)
    public List<SolicitudVerificacion> listarPendientes() {
        return solicitudRepository.findAllPendientes();
    }

    @Transactional
    public SolicitudVerificacion resolverSolicitud(Long adminUsuarioId, Long solicitudId, ResolverSolicitudRequest req) {
        SolicitudVerificacion solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        if (!solicitud.estado().equals(SolicitudVerificacion.ESTADO_PENDIENTE)) {
            throw new IllegalStateException("La solicitud ya fue resuelta");
        }

        SolicitudVerificacion actualizada = new SolicitudVerificacion(
                solicitud.id(),
                solicitud.cuidadorId(),
                solicitud.fechaSolicitud(),
                OffsetDateTime.now(),
                req.nuevoEstado(),
                req.observaciones(),
                adminUsuarioId,
                solicitud.createdAt(),
                solicitud.updatedAt(),
                solicitud.documentos()
        );

        SolicitudVerificacion guardada = solicitudRepository.update(actualizada);

        // Si fue aprobada, actualizar el estado del Cuidador
        if (req.nuevoEstado().equals(SolicitudVerificacion.ESTADO_APROBADA)) {
            Cuidador cuidador = cuidadorRepository.findById(solicitud.cuidadorId())
                    .orElseThrow();
            
            Cuidador cuidadorAprobado = new Cuidador(
                    cuidador.id(),
                    cuidador.usuarioId(),
                    cuidador.presentacion(),
                    cuidador.anosExperiencia(),
                    cuidador.tarifaReferencial(),
                    Cuidador.ESTADO_VERIFICACION_VERIFICADO, // <--- MAGIA
                    cuidador.estadoPublicacion(),
                    cuidador.calificacionPromedio(),
                    cuidador.createdAt(),
                    cuidador.updatedAt()
            );
            cuidadorRepository.update(cuidadorAprobado);
        }

        return guardada;
    }
}
