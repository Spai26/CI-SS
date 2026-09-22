package com.cuidar.api.reservas.api;

import com.cuidar.api.auth.service.security.CurrentUser;
import com.cuidar.api.common.web.ApiPaths;
import com.cuidar.api.reservas.api.dto.CambiarEstadoReservaRequest;
import com.cuidar.api.reservas.api.dto.CrearReservaRequest;
import com.cuidar.api.reservas.api.dto.ReservaResponse;
import com.cuidar.api.reservas.domain.Reserva;
import com.cuidar.api.reservas.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.V1 + "/reservas")
public class ReservaController {

    private final ReservaService reservaService;
    private final CurrentUser currentUser;

    public ReservaController(ReservaService reservaService, CurrentUser currentUser) {
        this.reservaService = reservaService;
        this.currentUser = currentUser;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservaResponse crearReserva(@RequestBody @Valid CrearReservaRequest request) {
        Long usuarioId = currentUser.getCurrentUserId();
        Reserva reserva = reservaService.crearReserva(usuarioId, request);
        return mapToResponse(reserva);
    }

    @PutMapping("/{id}/estado")
    public ReservaResponse cambiarEstado(@PathVariable Long id, @RequestBody @Valid CambiarEstadoReservaRequest request) {
        // En una implementación real, aquí se verificaría que el usuario tenga permisos (sea familia o cuidador involucrado)
        Reserva reserva = reservaService.cambiarEstado(id, request);
        return mapToResponse(reserva);
    }

    @GetMapping("/familia")
    public List<ReservaResponse> listarMisReservasFamilia() {
        Long usuarioId = currentUser.getCurrentUserId();
        return reservaService.listarPorFamilia(usuarioId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @GetMapping("/cuidador")
    public List<ReservaResponse> listarMisReservasCuidador() {
        Long usuarioId = currentUser.getCurrentUserId();
        return reservaService.listarPorCuidador(usuarioId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ReservaResponse mapToResponse(Reserva r) {
        return new ReservaResponse(
                r.id(),
                r.familiaId(),
                r.cuidadorId(),
                r.adultoMayorId(),
                r.fechaInicio(),
                r.fechaFin(),
                r.modalidad(),
                r.horasEstimadas(),
                r.montoTotal(),
                r.estado(),
                r.motivoCancelacion(),
                r.createdAt(),
                r.updatedAt()
        );
    }
}
