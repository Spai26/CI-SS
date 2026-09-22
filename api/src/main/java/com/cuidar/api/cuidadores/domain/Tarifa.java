package com.cuidar.api.cuidadores.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public record Tarifa(
        Long id,
        Long cuidadorId,
        String modalidad,
        BigDecimal monto,
        LocalDate fechaVigenciaDesde,
        LocalDate fechaVigenciaHasta,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
