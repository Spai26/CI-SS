package com.cuidar.api.reportes.api;

import com.cuidar.api.auth.service.security.CurrentUser;
import com.cuidar.api.common.web.ApiPaths;
import com.cuidar.api.reportes.api.dto.CrearReporteDiarioRequest;
import com.cuidar.api.reportes.api.dto.ReporteDiarioResponse;
import com.cuidar.api.reportes.domain.ReporteDiario;
import com.cuidar.api.reportes.service.ReporteDiarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.V1 + "/reservas/{reservaId}/reportes")
public class ReporteDiarioController {

    private final ReporteDiarioService reporteService;
    private final CurrentUser currentUser;

    public ReporteDiarioController(ReporteDiarioService reporteService, CurrentUser currentUser) {
        this.reporteService = reporteService;
        this.currentUser = currentUser;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReporteDiarioResponse crearReporte(
            @PathVariable Long reservaId,
            @RequestBody @Valid CrearReporteDiarioRequest request
    ) {
        Long cuidadorId = currentUser.getCurrentUserId();
        ReporteDiario reporte = reporteService.crearReporte(cuidadorId, reservaId, request);
        return mapToResponse(reporte);
    }

    @GetMapping
    public List<ReporteDiarioResponse> listarReportes(@PathVariable Long reservaId) {
        Long usuarioId = currentUser.getCurrentUserId();
        return reporteService.listarReportes(usuarioId, reservaId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ReporteDiarioResponse mapToResponse(ReporteDiario r) {
        return new ReporteDiarioResponse(
                r.id(),
                r.reservaId(),
                r.fecha(),
                r.horaInicio(),
                r.horaFin(),
                r.actividades(),
                r.observaciones(),
                r.estadoAnimo(),
                r.createdAt(),
                r.updatedAt()
        );
    }
}
