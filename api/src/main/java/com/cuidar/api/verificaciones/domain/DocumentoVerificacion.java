package com.cuidar.api.verificaciones.domain;

import java.time.OffsetDateTime;

public record DocumentoVerificacion(
        Long id,
        Long solicitudId,
        String tipoDocumento,
        String archivoUrl,
        String estado,
        String observaciones,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static final String TIPO_IDENTIDAD = "IDENTIDAD";
    public static final String TIPO_ANTECEDENTES = "ANTECEDENTES";
    public static final String TIPO_CV = "CV";
}
