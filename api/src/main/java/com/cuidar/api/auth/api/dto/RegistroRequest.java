package com.cuidar.api.auth.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Registration request payload.
 *
 * <p>All fields are validated at the controller boundary; bad requests are
 * mapped to RFC 7807 problem+json by {@code GlobalExceptionHandler}.</p>
 */
public record RegistroRequest(

        @NotBlank
        @Email
        @Size(max = 255)
        String email,

        @NotBlank
        @Size(min = 8, max = 100)
        String password,

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