package com.cuidar.api.verificaciones.api;

import com.cuidar.api.auth.service.security.CurrentUser;
import com.cuidar.api.common.web.ApiPaths;
import com.cuidar.api.verificaciones.api.dto.ResolverSolicitudRequest;
import com.cuidar.api.verificaciones.api.dto.SolicitudResponse;
import com.cuidar.api.verificaciones.domain.DocumentoVerificacion;
import com.cuidar.api.verificaciones.domain.SolicitudVerificacion;
import com.cuidar.api.verificaciones.service.VerificacionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.V1 + "/admin/verificaciones")
public class VerificacionAdminController {

    private final VerificacionService verificacionService;
    private final CurrentUser currentUser;

    public VerificacionAdminController(VerificacionService verificacionService, CurrentUser currentUser) {
        this.verificacionService = verificacionService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public List<SolicitudResponse> listarPendientes() {
        return verificacionService.listarPendientes()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @PutMapping("/{id}/resolucion")
    public SolicitudResponse resolverSolicitud(
            @PathVariable Long id,
            @RequestBody @Valid ResolverSolicitudRequest request
    ) {
        Long adminId = currentUser.getCurrentUserId();
        SolicitudVerificacion resuelta = verificacionService.resolverSolicitud(adminId, id, request);
        return mapToResponse(resuelta);
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
