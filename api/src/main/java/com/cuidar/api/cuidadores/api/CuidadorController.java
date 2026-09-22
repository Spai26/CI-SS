package com.cuidar.api.cuidadores.api;

import com.cuidar.api.auth.service.security.CurrentUser;
import com.cuidar.api.common.web.ApiPaths;
import com.cuidar.api.cuidadores.api.dto.ActualizarCuidadorRequest;
import com.cuidar.api.cuidadores.api.dto.BusquedaCuidadorRequest;
import com.cuidar.api.cuidadores.api.dto.CuidadorResponse;
import com.cuidar.api.cuidadores.api.dto.ExperienciaLaboralRequest;
import com.cuidar.api.cuidadores.api.dto.ExperienciaLaboralResponse;
import com.cuidar.api.cuidadores.domain.Cuidador;
import com.cuidar.api.cuidadores.domain.ExperienciaLaboral;
import com.cuidar.api.cuidadores.service.CuidadorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.CUIDADORES)
public class CuidadorController {

    private final CuidadorService cuidadorService;
    private final CurrentUser currentUser;

    public CuidadorController(CuidadorService cuidadorService, CurrentUser currentUser) {
        this.cuidadorService = cuidadorService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public List<CuidadorResponse> buscarCuidadores(@Valid BusquedaCuidadorRequest filtros) {
        return cuidadorService.buscarCuidadores(filtros)
                .stream()
                .map(this::mapToCuidadorResponse)
                .toList();
    }

    @GetMapping("/me")
    public CuidadorResponse getMiPerfil() {
        Long usuarioId = currentUser.getCurrentUserId();
        Cuidador c = cuidadorService.getCuidadorByUsuarioId(usuarioId);
        return mapToCuidadorResponse(c);
    }

    @PutMapping("/me")
    public CuidadorResponse actualizarMiPerfil(@RequestBody @Valid ActualizarCuidadorRequest request) {
        Long usuarioId = currentUser.getCurrentUserId();
        Cuidador c = cuidadorService.actualizarCuidador(usuarioId, request);
        return mapToCuidadorResponse(c);
    }

    @GetMapping("/me/experiencias")
    public List<ExperienciaLaboralResponse> listarExperiencias() {
        Long usuarioId = currentUser.getCurrentUserId();
        return cuidadorService.listarExperiencias(usuarioId)
                .stream()
                .map(this::mapToExperienciaResponse)
                .toList();
    }

    @PostMapping("/me/experiencias")
    @ResponseStatus(HttpStatus.CREATED)
    public ExperienciaLaboralResponse agregarExperiencia(@RequestBody @Valid ExperienciaLaboralRequest request) {
        Long usuarioId = currentUser.getCurrentUserId();
        ExperienciaLaboral exp = cuidadorService.agregarExperiencia(usuarioId, request);
        return mapToExperienciaResponse(exp);
    }

    private CuidadorResponse mapToCuidadorResponse(Cuidador c) {
        return new CuidadorResponse(
                c.id(),
                c.usuarioId(),
                c.presentacion(),
                c.anosExperiencia(),
                c.tarifaReferencial(),
                c.estadoVerificacion(),
                c.estadoPublicacion(),
                c.calificacionPromedio(),
                c.createdAt(),
                c.updatedAt()
        );
    }

    private ExperienciaLaboralResponse mapToExperienciaResponse(ExperienciaLaboral exp) {
        return new ExperienciaLaboralResponse(
                exp.id(),
                exp.cuidadorId(),
                exp.empresa(),
                exp.cargo(),
                exp.fechaInicio(),
                exp.fechaFin(),
                exp.descripcion(),
                exp.createdAt(),
                exp.updatedAt()
        );
    }
}
