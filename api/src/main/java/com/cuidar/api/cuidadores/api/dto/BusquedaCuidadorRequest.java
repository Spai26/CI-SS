package com.cuidar.api.cuidadores.api.dto;

import java.math.BigDecimal;

public record BusquedaCuidadorRequest(
        Integer experienciaMin,
        BigDecimal tarifaMax,
        BigDecimal calificacionMin
) {
}
