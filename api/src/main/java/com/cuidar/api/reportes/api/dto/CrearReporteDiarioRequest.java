package com.cuidar.api.reportes.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record CrearReporteDiarioRequest(
        @NotNull LocalDate fecha,
        @NotNull LocalTime horaInicio,
        @NotNull LocalTime horaFin,
        @NotBlank String actividades,
        String observaciones,
        @NotBlank String estadoAnimo
) {
}
