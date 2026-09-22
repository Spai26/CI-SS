package com.cuidar.api.reservas.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CambiarEstadoReservaRequest(
        @NotBlank
        @Pattern(regexp = "^(CONFIRMADA|RECHAZADA|CANCELADA|FINALIZADA|EN_CURSO)$")
        String nuevoEstado,
        
        String motivoCancelacion
) {
}
