package com.cuidar.api.reservas.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public record ReservaResponse(
        Long id,
        @JsonProperty("familia_id")
        Long familiaId,
        @JsonProperty("cuidador_id")
        Long cuidadorId,
        @JsonProperty("adulto_mayor_id")
        Long adultoMayorId,
        @JsonProperty("fecha_inicio")
        LocalDate fechaInicio,
        @JsonProperty("fecha_fin")
        LocalDate fechaFin,
        String modalidad,
        @JsonProperty("horas_estimadas")
        BigDecimal horasEstimadas,
        @JsonProperty("monto_total")
        BigDecimal montoTotal,
        String estado,
        @JsonProperty("motivo_cancelacion")
        String motivoCancelacion,
        @JsonProperty("created_at")
        OffsetDateTime createdAt,
        @JsonProperty("updated_at")
        OffsetDateTime updatedAt
) {
}
