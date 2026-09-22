package com.cuidar.api.familias.api.dto;

import jakarta.validation.constraints.Size;

public record ActualizarFamiliaRequest(
        @Size(max = 500)
        String direccion,

        String preferenciasBusqueda
) {
}
