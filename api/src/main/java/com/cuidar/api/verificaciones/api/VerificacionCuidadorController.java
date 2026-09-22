package com.cuidar.api.verificaciones.api;

import com.cuidar.api.auth.service.security.CurrentUser;
import com.cuidar.api.common.web.ApiPaths;
import com.cuidar.api.verificaciones.api.dto.CrearSolicitudRequest;
import com.cuidar.api.verificaciones.api.dto.SolicitudResponse;
import com.cuidar.api.verificaciones.domain.DocumentoVerificacion;
import com.cuidar.api.verificaciones.domain.SolicitudVerificacion;
import com.cuidar.api.verificaciones.service.VerificacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.V1 + "/cuidadores/me/verificacion")
public class VerificacionCuidadorController {

    private final VerificacionService verificacionService;
    private final CurrentUser currentUser;

    public VerificacionCuidadorController(VerificacionService verificacionService, CurrentUser currentUser) {
        this.verificacionService = verificacionService;
        this.currentUser = currentUser;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SolicitudResponse crearSolicitud(@RequestBody @Valid CrearSolicitudRequest request) {
        Long usuarioId = currentUser.getCurrentUserId();
        SolicitudVerificacion creada = verificacionService.crearSolicitud(usuarioId, request);
        return mapToResponse(creada);
    }

    private SolicitudResponse mapToResponse(SolicitudVerificacion s) {
        List<SolicitudResponse.DocumentoVerificacionResponse> docs = s.documentos() == null ? List.of() :
                s.documentos().stream()
                        .map(this::mapDocToResponse)
                        .toList();

        return new SolicitudResponse(
                s.id(),
                s.cuidadorId(),
                s.fechaSolicitud(),
                s.fechaResolucion(),
                s.estado(),
                s.observaciones(),
                s.revisadaPor(),
                docs
        );
    }

    private SolicitudResponse.DocumentoVerificacionResponse mapDocToResponse(DocumentoVerificacion d) {
        return new SolicitudResponse.DocumentoVerificacionResponse(
                d.id(),
                d.tipoDocumento(),
                d.archivoUrl(),
                d.estado()
        );
    }
}
