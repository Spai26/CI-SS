package com.cuidar.api.reservas.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public record Reserva(
        Long id,
        Long familiaId,
        Long cuidadorId,
        Long adultoMayorId,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        String modalidad,
        BigDecimal horasEstimadas,
        BigDecimal montoTotal,
        String estado,
        String motivoCancelacion,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static final String ESTADO_PENDIENTE = "PENDIENTE";
    public static final String ESTADO_CONFIRMADA = "CONFIRMADA";
    public static final String ESTADO_RECHAZADA = "RECHAZADA";
    public static final String ESTADO_EN_CURSO = "EN_CURSO";
    public static final String ESTADO_FINALIZADA = "FINALIZADA";
    public static final String ESTADO_CANCELADA = "CANCELADA";
}
