package com.cuidar.api.auth.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Update-profile payload for PUT /api/v1/auth/me.
 */
public record UpdatePerfilRequest(

        @NotBlank
        @Size(max = 100)
        String nombre,

        @NotBlank
        @Size(max = 100)
        String apellido,

        @Pattern(regexp = "\\+?\\d{6,20}", message = "telefono must be E.164 or local digits")
        String telefono
) {
}