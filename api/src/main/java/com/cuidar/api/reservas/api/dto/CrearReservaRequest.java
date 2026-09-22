package com.cuidar.api.reservas.api.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CrearReservaRequest(
        @NotNull Long cuidadorId,
        @NotNull Long adultoMayorId,
        @NotNull LocalDate fechaInicio,
        LocalDate fechaFin,
        String modalidad,
        BigDecimal horasEstimadas
) {
}
