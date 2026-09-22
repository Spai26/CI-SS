package com.cuidar.api.cuidadores.domain;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record ExperienciaLaboral(
        Long id,
        Long cuidadorId,
        String empresa,
        String cargo,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        String descripcion,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
