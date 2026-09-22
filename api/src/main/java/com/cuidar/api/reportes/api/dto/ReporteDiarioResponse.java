package com.cuidar.api.reportes.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;

public record ReporteDiarioResponse(
        Long id,
        @JsonProperty("reserva_id")
        Long reservaId,
        LocalDate fecha,
        @JsonProperty("hora_inicio")
        LocalTime horaInicio,
        @JsonProperty("hora_fin")
        LocalTime horaFin,
        String actividades,
        String observaciones,
        @JsonProperty("estado_animo")
        String estadoAnimo,
        @JsonProperty("created_at")
        OffsetDateTime createdAt,
        @JsonProperty("updated_at")
        OffsetDateTime updatedAt
) {
}
