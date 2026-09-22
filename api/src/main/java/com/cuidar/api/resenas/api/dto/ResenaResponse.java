package com.cuidar.api.resenas.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public record ResenaResponse(
        Long id,
        @JsonProperty("reserva_id")
        Long reservaId,
        @JsonProperty("autor_id")
        Long autorId,
        @JsonProperty("destinatario_id")
        Long destinatarioId,
        Short calificacion,
        String comentario,
        OffsetDateTime fecha,
        @JsonProperty("created_at")
        OffsetDateTime createdAt
) {
}
