package com.cuidar.api.familias.api.dto;

import com.cuidar.api.familias.domain.GeneroAdulto;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public record AdultoMayorResponse(
        Long id,
        @JsonProperty("familia_id")
        Long familiaId,
        String nombre,
        String apellido,
        @JsonProperty("fecha_nacimiento")
        LocalDate fechaNacimiento,
        GeneroAdulto genero,
        @JsonProperty("condicion_salud")
        String condicionSalud,
        String notas,
        @JsonProperty("created_at")
        OffsetDateTime createdAt,
        @JsonProperty("updated_at")
        OffsetDateTime updatedAt
) {
}
