package com.cuidar.api.familias.domain;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Pure-Java domain model for AdultoMayor.
 */
public record AdultoMayor(
        Long id,
        Long familiaId,
        String nombre,
        String apellido,
        LocalDate fechaNacimiento,
        GeneroAdulto genero,
        String condicionSalud,
        String notas,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
