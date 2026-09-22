package com.cuidar.api.verificaciones.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.List;

public record SolicitudResponse(
        Long id,
        @JsonProperty("cuidador_id")
        Long cuidadorId,
        @JsonProperty("fecha_solicitud")
        OffsetDateTime fechaSolicitud,
        @JsonProperty("fecha_resolucion")
        OffsetDateTime fechaResolucion,
        String estado,
        String observaciones,
        @JsonProperty("revisada_por")
        Long revisadaPor,
        List<DocumentoVerificacionResponse> documentos
) {
    public record DocumentoVerificacionResponse(
            Long id,
            @JsonProperty("tipo_documento")
            String tipoDocumento,
            @JsonProperty("archivo_url")
            String archivoUrl,
            String estado
    ) {}
}
