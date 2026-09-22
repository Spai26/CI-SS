package com.cuidar.api.cuidadores.repository;

import com.cuidar.api.cuidadores.domain.Cuidador;

import com.cuidar.api.cuidadores.api.dto.BusquedaCuidadorRequest;

import java.util.List;
import java.util.Optional;

public interface CuidadorRepository {
    Optional<Cuidador> findById(Long id);
    Optional<Cuidador> findByUsuarioId(Long usuarioId);
    Cuidador insert(Cuidador cuidador);
    Cuidador update(Cuidador cuidador);
    List<Cuidador> buscarCuidadores(BusquedaCuidadorRequest filtros);
}
