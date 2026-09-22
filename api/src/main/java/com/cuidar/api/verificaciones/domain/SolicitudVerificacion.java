package com.cuidar.api.verificaciones.domain;

import java.time.OffsetDateTime;
import java.util.List;

public record SolicitudVerificacion(
        Long id,
        Long cuidadorId,
        OffsetDateTime fechaSolicitud,
        OffsetDateTime fechaResolucion,
        String estado,
        String observaciones,
        Long revisadaPor,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        List<DocumentoVerificacion> documentos
) {
    public static final String ESTADO_PENDIENTE = "PENDIENTE";
    public static final String ESTADO_APROBADA = "APROBADA";
    public static final String ESTADO_RECHAZADA = "RECHAZADA";
}
