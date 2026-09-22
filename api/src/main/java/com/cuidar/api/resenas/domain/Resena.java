package com.cuidar.api.resenas.domain;

import java.time.OffsetDateTime;

public record Resena(
        Long id,
        Long reservaId,
        Long autorId,
        Long destinatarioId,
        Short calificacion,
        String comentario,
        OffsetDateTime fecha,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
