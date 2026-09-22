package com.cuidar.api.reservas.service;

import com.cuidar.api.cuidadores.domain.Cuidador;
import com.cuidar.api.cuidadores.repository.CuidadorRepository;
import com.cuidar.api.familias.domain.Familia;
import com.cuidar.api.familias.repository.FamiliaRepository;
import com.cuidar.api.reservas.api.dto.CambiarEstadoReservaRequest;
import com.cuidar.api.reservas.api.dto.CrearReservaRequest;
import com.cuidar.api.reservas.domain.Reserva;
import com.cuidar.api.reservas.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final FamiliaRepository familiaRepository;
    private final CuidadorRepository cuidadorRepository;

    public ReservaService(ReservaRepository reservaRepository, FamiliaRepository familiaRepository, CuidadorRepository cuidadorRepository) {
        this.reservaRepository = reservaRepository;
        this.familiaRepository = familiaRepository;
        this.cuidadorRepository = cuidadorRepository;
    }

    @Transactional
    public Reserva crearReserva(Long familiaUsuarioId, CrearReservaRequest request) {
        Familia familia = familiaRepository.findByUsuarioId(familiaUsuarioId)
                .orElseThrow(() -> new IllegalArgumentException("La familia no existe"));

        Cuidador cuidador = cuidadorRepository.findById(request.cuidadorId())
                .orElseThrow(() -> new IllegalArgumentException("El cuidador no existe"));

        // Here we could validate that AdultoMayor belongs to this Familia

        // Calculate montoTotal based on tarifaReferencial and horasEstimadas
        BigDecimal montoTotal = BigDecimal.ZERO;
        if (request.horasEstimadas() != null && cuidador.tarifaReferencial() != null) {
            montoTotal = request.horasEstimadas().multiply(cuidador.tarifaReferencial());
        }

        Reserva reserva = new Reserva(
                null,
                familia.id(),
                cuidador.id(),
                request.adultoMayorId(),
                request.fechaInicio(),
                request.fechaFin(),
                request.modalidad(),
                request.horasEstimadas(),
                montoTotal,
                Reserva.ESTADO_PENDIENTE,
                null,
                null,
                null
        );

        return reservaRepository.insert(reserva);
    }

    @Transactional
    public Reserva cambiarEstado(Long id, CambiarEstadoReservaRequest request) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        Reserva modificada = new Reserva(
                reserva.id(),
                reserva.familiaId(),
                reserva.cuidadorId(),
                reserva.adultoMayorId(),
                reserva.fechaInicio(),
                reserva.fechaFin(),
                reserva.modalidad(),
                reserva.horasEstimadas(),
                reserva.montoTotal(),
                request.nuevoEstado(),
                request.motivoCancelacion(),
                reserva.createdAt(),
                reserva.updatedAt()
        );
        return reservaRepository.update(modificada);
    }

    @Transactional(readOnly = true)
    public List<Reserva> listarPorFamilia(Long familiaUsuarioId) {
        return familiaRepository.findByUsuarioId(familiaUsuarioId)
                .map(familia -> reservaRepository.findByFamiliaId(familia.id()))
                .orElse(List.of());
    }

    @Transactional(readOnly = true)
    public List<Reserva> listarPorCuidador(Long cuidadorUsuarioId) {
        return cuidadorRepository.findByUsuarioId(cuidadorUsuarioId)
                .map(cuidador -> reservaRepository.findByCuidadorId(cuidador.id()))
                .orElse(List.of());
    }
}
