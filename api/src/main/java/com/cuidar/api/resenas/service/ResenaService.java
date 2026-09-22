package com.cuidar.api.resenas.service;

import com.cuidar.api.cuidadores.domain.Cuidador;
import com.cuidar.api.cuidadores.repository.CuidadorRepository;
import com.cuidar.api.familias.domain.Familia;
import com.cuidar.api.familias.repository.FamiliaRepository;
import com.cuidar.api.resenas.api.dto.CrearResenaRequest;
import com.cuidar.api.resenas.domain.Resena;
import com.cuidar.api.resenas.repository.ResenaRepository;
import com.cuidar.api.reservas.domain.Reserva;
import com.cuidar.api.reservas.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class ResenaService {

    private final ResenaRepository resenaRepository;
    private final ReservaRepository reservaRepository;
    private final FamiliaRepository familiaRepository;
    private final CuidadorRepository cuidadorRepository;

    public ResenaService(ResenaRepository resenaRepository, ReservaRepository reservaRepository, FamiliaRepository familiaRepository, CuidadorRepository cuidadorRepository) {
        this.resenaRepository = resenaRepository;
        this.reservaRepository = reservaRepository;
        this.familiaRepository = familiaRepository;
        this.cuidadorRepository = cuidadorRepository;
    }

    @Transactional
    public Resena crearResenaDeFamiliaACuidador(Long familiaUsuarioId, Long reservaId, CrearResenaRequest req) {
        Familia familia = familiaRepository.findByUsuarioId(familiaUsuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Familia no encontrada"));

        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        if (!reserva.familiaId().equals(familia.id())) {
            throw new IllegalArgumentException("La reserva no pertenece a esta familia");
        }

        if (!reserva.estado().equals(Reserva.ESTADO_CONFIRMADA)) {
            throw new IllegalStateException("Solo se pueden dejar reseñas de reservas confirmadas");
        }

        if (reserva.fechaInicio().isAfter(java.time.LocalDate.now())) {
            throw new IllegalStateException("El servicio aún no ha comenzado/terminado");
        }

        Resena resena = new Resena(
                null,
                reserva.id(),
                familia.usuarioId(), // El autor es el usuario de la Familia
                reserva.cuidadorId(), // El destinatario es el Cuidador (no su usuario)
                req.calificacion(),
                req.comentario(),
                OffsetDateTime.now(),
                null,
                null
        );

        Resena guardada = resenaRepository.insert(resena);

        // Recalcular promedio y actualizar Cuidador
        BigDecimal nuevoPromedio = resenaRepository.calcularPromedioPorDestinatario(reserva.cuidadorId());
        
        Cuidador cuidador = cuidadorRepository.findById(reserva.cuidadorId()).orElseThrow();
        Cuidador actualizado = new Cuidador(
                cuidador.id(),
                cuidador.usuarioId(),
                cuidador.presentacion(),
                cuidador.anosExperiencia(),
                cuidador.tarifaReferencial(),
                cuidador.estadoVerificacion(),
                cuidador.estadoPublicacion(),
                nuevoPromedio, // <--- Actualizamos
                cuidador.createdAt(),
                cuidador.updatedAt()
        );
        cuidadorRepository.update(actualizado);

        return guardada;
    }

    @Transactional(readOnly = true)
    public List<Resena> listarResenasDeCuidador(Long cuidadorId) {
        return resenaRepository.findByDestinatarioId(cuidadorId);
    }
}
