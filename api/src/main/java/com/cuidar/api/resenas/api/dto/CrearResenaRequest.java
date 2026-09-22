package com.cuidar.api.resenas.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CrearResenaRequest(
        @NotNull
        @Min(1)
        @Max(5)
        Short calificacion,

        String comentario
) {
}
