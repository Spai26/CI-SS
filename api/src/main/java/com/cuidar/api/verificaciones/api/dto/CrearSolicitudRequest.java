package com.cuidar.api.verificaciones.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CrearSolicitudRequest(
        @NotEmpty
        @Valid
        List<DocumentoVerificacionRequest> documentos
) {
}
