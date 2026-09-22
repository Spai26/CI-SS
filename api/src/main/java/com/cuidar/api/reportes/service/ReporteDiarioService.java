package com.cuidar.api.reportes.service;

import com.cuidar.api.cuidadores.domain.Cuidador;
import com.cuidar.api.cuidadores.repository.CuidadorRepository;
import com.cuidar.api.familias.domain.Familia;
import com.cuidar.api.familias.repository.FamiliaRepository;
import com.cuidar.api.reportes.api.dto.CrearReporteDiarioRequest;
import com.cuidar.api.reportes.domain.ReporteDiario;
import com.cuidar.api.reportes.repository.ReporteDiarioRepository;
import com.cuidar.api.reservas.domain.Reserva;
import com.cuidar.api.reservas.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReporteDiarioService {

    private final ReporteDiarioRepository reporteRepository;
    private final ReservaRepository reservaRepository;
    private final CuidadorRepository cuidadorRepository;
    private final FamiliaRepository familiaRepository;

    public ReporteDiarioService(ReporteDiarioRepository reporteRepository, ReservaRepository reservaRepository, CuidadorRepository cuidadorRepository, FamiliaRepository familiaRepository) {
        this.reporteRepository = reporteRepository;
        this.reservaRepository = reservaRepository;
        this.cuidadorRepository = cuidadorRepository;
        this.familiaRepository = familiaRepository;
    }

    @Transactional
    public ReporteDiario crearReporte(Long cuidadorUsuarioId, Long reservaId, CrearReporteDiarioRequest req) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        Cuidador cuidador = cuidadorRepository.findByUsuarioId(cuidadorUsuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Cuidador no encontrado"));

        if (!reserva.cuidadorId().equals(cuidador.id())) {
            throw new IllegalArgumentException("No puedes enviar reportes a una reserva que no te pertenece");
        }

        if (!reserva.estado().equals(Reserva.ESTADO_CONFIRMADA) && !reserva.estado().equals(Reserva.ESTADO_EN_CURSO)) {
            throw new IllegalStateException("Solo puedes enviar reportes de reservas activas");
        }

        ReporteDiario reporte = new ReporteDiario(
                null,
                reserva.id(),
                req.fecha(),
                req.horaInicio(),
                req.horaFin(),
                req.actividades(),
                req.observaciones(),
                req.estadoAnimo(),
                null,
                null
        );

        return reporteRepository.insert(reporte);
    }

    @Transactional(readOnly = true)
    public List<ReporteDiario> listarReportes(Long usuarioId, Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        boolean isCuidador = cuidadorRepository.findByUsuarioId(usuarioId)
                .map(c -> c.id().equals(reserva.cuidadorId()))
                .orElse(false);

        boolean isFamilia = familiaRepository.findByUsuarioId(usuarioId)
                .map(f -> f.id().equals(reserva.familiaId()))
                .orElse(false);

        if (!isCuidador && !isFamilia) {
            throw new IllegalArgumentException("No tienes permiso para ver estos reportes");
        }

        return reporteRepository.findByReservaId(reservaId);
    }
}
