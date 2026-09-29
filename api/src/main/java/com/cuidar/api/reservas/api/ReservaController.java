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

/**
 * Controlador REST que expone las operaciones relacionadas con las reservas de cuidadores.
 * <p>
 * Este controlador permite gestionar el ciclo de vida de una reserva,
 * incluyendo su creación, cambio de estado (aceptación, rechazo, cancelación)
 * y listado tanto para familias como para cuidadores.
 * </p>
 *
 * @author Equipo de Desarrollo CI-SS (Saboya Fulca, Avalos Ibarra, Ccencho Tipismana, Jimenez Sanchez)
 * @version 1.0.0
 * @since 2026
 */
@RestController
@RequestMapping(ApiPaths.V1 + "/reservas")
public class ReservaController {

    private final ReservaService reservaService;
    private final CurrentUser currentUser;

    public ReservaController(ReservaService reservaService, CurrentUser currentUser) {
        this.reservaService = reservaService;
        this.currentUser = currentUser;
    }

    /**
     * Crea una nueva reserva por parte de un usuario con perfil de Familia.
     * <p>
     * Este endpoint captura los detalles solicitados de la reserva, como fechas, modalidad
     * y el cuidador seleccionado, procesándola y calculando los costos estimados.
     * </p>
     *
     * @param request el objeto con los datos necesarios para crear la reserva, validado automáticamente.
     * @return un objeto {@link ReservaResponse} que representa la reserva recién creada con sus datos generados.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservaResponse crearReserva(@RequestBody @Valid CrearReservaRequest request) {
        Long usuarioId = currentUser.getCurrentUserId();
        Reserva reserva = reservaService.crearReserva(usuarioId, request);
        return mapToResponse(reserva);
    }

    /**
     * Cambia el estado actual de una reserva existente.
     * <p>
     * Típicamente utilizado por cuidadores para ACEPTAR/RECHAZAR, o por familias para CANCELAR
     * una reserva en curso.
     * </p>
     *
     * @param id el identificador único de la reserva a modificar.
     * @param request el objeto que contiene el nuevo estado y el motivo (opcional) del cambio.
     * @return un objeto {@link ReservaResponse} con la reserva actualizada.
     */
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
