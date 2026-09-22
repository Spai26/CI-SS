package com.cuidar.api.cuidadores.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record Cuidador(
        Long id,
        Long usuarioId,
        String presentacion,
        Integer anosExperiencia,
        BigDecimal tarifaReferencial,
        String estadoVerificacion,
        String estadoPublicacion,
        BigDecimal calificacionPromedio,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static final String ESTADO_VERIFICACION_NO_VERIFICADO = "NO_VERIFICADO";
    public static final String ESTADO_VERIFICACION_PENDIENTE = "PENDIENTE";
    public static final String ESTADO_VERIFICACION_VERIFICADO = "VERIFICADO";
    public static final String ESTADO_VERIFICACION_RECHAZADO = "RECHAZADO";

    public static final String ESTADO_PUBLICACION_BORRADOR = "BORRADOR";
    public static final String ESTADO_PUBLICACION_PUBLICADO = "PUBLICADO";
    public static final String ESTADO_PUBLICACION_PAUSADO = "PAUSADO";
    public static final String ESTADO_PUBLICACION_RETIRADO = "RETIRADO";
}
