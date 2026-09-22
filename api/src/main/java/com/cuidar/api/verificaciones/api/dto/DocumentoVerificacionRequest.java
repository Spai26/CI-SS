package com.cuidar.api.verificaciones.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record DocumentoVerificacionRequest(
        @NotBlank
        @Pattern(regexp = "^(IDENTIDAD|ANTECEDENTES|CV)$")
        String tipoDocumento,

        @NotBlank
        String archivoUrl
) {
}
