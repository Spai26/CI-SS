package com.cuidar.api.familias.api.dto;

import com.cuidar.api.familias.domain.GeneroAdulto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AdultoMayorRequest(
        @NotBlank
        @Size(max = 100)
        String nombre,

        @NotBlank
        @Size(max = 100)
        String apellido,

        LocalDate fechaNacimiento,

        GeneroAdulto genero,

        String condicionSalud,

        String notas
) {
}
