package com.cuidar.api.resenas.api;

import com.cuidar.api.auth.service.security.CurrentUser;
import com.cuidar.api.common.web.ApiPaths;
import com.cuidar.api.resenas.api.dto.CrearResenaRequest;
import com.cuidar.api.resenas.api.dto.ResenaResponse;
import com.cuidar.api.resenas.domain.Resena;
import com.cuidar.api.resenas.service.ResenaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.V1)
public class ResenaController {

    private final ResenaService resenaService;
    private final CurrentUser currentUser;

    public ResenaController(ResenaService resenaService, CurrentUser currentUser) {
        this.resenaService = resenaService;
        this.currentUser = currentUser;
    }

    @PostMapping("/reservas/{reservaId}/resenas")
    @ResponseStatus(HttpStatus.CREATED)
    public ResenaResponse crearResenaDeFamilia(
            @PathVariable Long reservaId,
            @RequestBody @Valid CrearResenaRequest request
    ) {
        Long familiaUsuarioId = currentUser.getCurrentUserId();
        Resena resena = resenaService.crearResenaDeFamiliaACuidador(familiaUsuarioId, reservaId, request);
        return mapToResponse(resena);
    }

    @GetMapping("/cuidadores/{cuidadorId}/resenas")
    public List<ResenaResponse> listarResenasDeCuidador(@PathVariable Long cuidadorId) {
        return resenaService.listarResenasDeCuidador(cuidadorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ResenaResponse mapToResponse(Resena r) {
        return new ResenaResponse(
                r.id(),
                r.reservaId(),
                r.autorId(),
                r.destinatarioId(),
                r.calificacion(),
                r.comentario(),
                r.fecha(),
                r.createdAt()
        );
    }
}
