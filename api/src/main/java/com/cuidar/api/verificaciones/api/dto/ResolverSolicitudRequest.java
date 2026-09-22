package com.cuidar.api.verificaciones.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ResolverSolicitudRequest(
        @NotBlank
        @Pattern(regexp = "^(APROBADA|RECHAZADA)$")
        String nuevoEstado,

        String observaciones
) {
}
