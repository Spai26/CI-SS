package com.cuidar.api.familias.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public record FamiliaResponse(
        Long id,
        Long usuarioId,
        String direccion,
        @JsonProperty("preferencias_busqueda")
        String preferenciasBusqueda,
        @JsonProperty("created_at")
        OffsetDateTime createdAt,
        @JsonProperty("updated_at")
        OffsetDateTime updatedAt
) {
}
