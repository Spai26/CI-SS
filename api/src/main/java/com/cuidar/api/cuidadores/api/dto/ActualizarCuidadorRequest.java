package com.cuidar.api.cuidadores.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ActualizarCuidadorRequest(
        @Size(max = 2000)
        String presentacion,

        @Min(0)
        Integer anosExperiencia,

        @Min(0)
        BigDecimal tarifaReferencial
) {
}
