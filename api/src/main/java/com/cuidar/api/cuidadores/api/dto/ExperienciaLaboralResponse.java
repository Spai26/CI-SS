package com.cuidar.api.cuidadores.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public record ExperienciaLaboralResponse(
        Long id,
        @JsonProperty("cuidador_id")
        Long cuidadorId,
        String empresa,
        String cargo,
        @JsonProperty("fecha_inicio")
        LocalDate fechaInicio,
        @JsonProperty("fecha_fin")
        LocalDate fechaFin,
        String descripcion,
        @JsonProperty("created_at")
        OffsetDateTime createdAt,
        @JsonProperty("updated_at")
        OffsetDateTime updatedAt
) {
}
