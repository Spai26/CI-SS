package com.cuidar.api.cuidadores.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record CuidadorResponse(
        Long id,
        @JsonProperty("usuario_id")
        Long usuarioId,
        String presentacion,
        @JsonProperty("anos_experiencia")
        Integer anosExperiencia,
        @JsonProperty("tarifa_referencial")
        BigDecimal tarifaReferencial,
        @JsonProperty("estado_verificacion")
        String estadoVerificacion,
        @JsonProperty("estado_publicacion")
        String estadoPublicacion,
        @JsonProperty("calificacion_promedio")
        BigDecimal calificacionPromedio,
        @JsonProperty("created_at")
        OffsetDateTime createdAt,
        @JsonProperty("updated_at")
        OffsetDateTime updatedAt
) {
}
