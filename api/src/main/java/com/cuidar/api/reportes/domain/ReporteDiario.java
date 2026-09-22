package com.cuidar.api.reportes.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;

public record ReporteDiario(
        Long id,
        Long reservaId,
        LocalDate fecha,
        LocalTime horaInicio,
        LocalTime horaFin,
        String actividades,
        String observaciones,
        String estadoAnimo,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
