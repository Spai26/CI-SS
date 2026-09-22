package com.cuidar.api.familias.domain;

import java.time.OffsetDateTime;

/**
 * Pure-Java domain model for a Familia.
 */
public record Familia(
        Long id,
        Long usuarioId,
        String direccion,
        String preferenciasBusqueda,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
