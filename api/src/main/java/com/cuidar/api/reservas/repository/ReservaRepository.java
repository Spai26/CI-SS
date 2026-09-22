package com.cuidar.api.reservas.repository;

import com.cuidar.api.reservas.domain.Reserva;

import java.util.List;
import java.util.Optional;

public interface ReservaRepository {
    Optional<Reserva> findById(Long id);
    List<Reserva> findByFamiliaId(Long familiaId);
    List<Reserva> findByCuidadorId(Long cuidadorId);
    Reserva insert(Reserva reserva);
    Reserva update(Reserva reserva);
}
