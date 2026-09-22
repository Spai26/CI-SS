package com.cuidar.api.cuidadores.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ExperienciaLaboralRequest(
        @NotBlank
        @Size(max = 200)
        String empresa,

        @NotBlank
        @Size(max = 200)
        String cargo,

        @NotNull
        LocalDate fechaInicio,

        LocalDate fechaFin,

        String descripcion
) {
}
