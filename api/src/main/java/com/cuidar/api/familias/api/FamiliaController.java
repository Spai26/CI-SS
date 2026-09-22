package com.cuidar.api.familias.api;

import com.cuidar.api.auth.service.security.CurrentUser;
import com.cuidar.api.common.web.ApiPaths;
import com.cuidar.api.familias.api.dto.ActualizarFamiliaRequest;
import com.cuidar.api.familias.api.dto.AdultoMayorRequest;
import com.cuidar.api.familias.api.dto.AdultoMayorResponse;
import com.cuidar.api.familias.api.dto.FamiliaResponse;
import com.cuidar.api.familias.domain.AdultoMayor;
import com.cuidar.api.familias.domain.Familia;
import com.cuidar.api.familias.service.FamiliaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.FAMILIAS)
public class FamiliaController {

    private final FamiliaService familiaService;
    private final CurrentUser currentUser;

    public FamiliaController(FamiliaService familiaService, CurrentUser currentUser) {
        this.familiaService = familiaService;
        this.currentUser = currentUser;
    }

    @GetMapping("/me")
    public FamiliaResponse getMiFamilia() {
        Long usuarioId = currentUser.getCurrentUserId();
        Familia f = familiaService.getFamiliaByUsuarioId(usuarioId);
        return mapToFamiliaResponse(f);
    }

    @PutMapping("/me")
    public FamiliaResponse actualizarMiFamilia(@RequestBody @Valid ActualizarFamiliaRequest request) {
        Long usuarioId = currentUser.getCurrentUserId();
        Familia f = familiaService.actualizarFamilia(usuarioId, request);
        return mapToFamiliaResponse(f);
    }

    @GetMapping("/me/adultos-mayores")
    public List<AdultoMayorResponse> listarAdultosMayores() {
        Long usuarioId = currentUser.getCurrentUserId();
        return familiaService.listarAdultosMayores(usuarioId)
                .stream()
                .map(this::mapToAdultoMayorResponse)
                .toList();
    }

    @PostMapping("/me/adultos-mayores")
    @ResponseStatus(HttpStatus.CREATED)
    public AdultoMayorResponse agregarAdultoMayor(@RequestBody @Valid AdultoMayorRequest request) {
        Long usuarioId = currentUser.getCurrentUserId();
        AdultoMayor a = familiaService.agregarAdultoMayor(usuarioId, request);
        return mapToAdultoMayorResponse(a);
    }

    private FamiliaResponse mapToFamiliaResponse(Familia f) {
        return new FamiliaResponse(
                f.id(),
                f.usuarioId(),
                f.direccion(),
                f.preferenciasBusqueda(),
                f.createdAt(),
                f.updatedAt()
        );
    }

    private AdultoMayorResponse mapToAdultoMayorResponse(AdultoMayor a) {
        return new AdultoMayorResponse(
                a.id(),
                a.familiaId(),
                a.nombre(),
                a.apellido(),
                a.fechaNacimiento(),
                a.genero(),
                a.condicionSalud(),
                a.notas(),
                a.createdAt(),
                a.updatedAt()
        );
    }
}
