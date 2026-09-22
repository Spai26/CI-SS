package com.cuidar.api.familias.repository;

import com.cuidar.api.familias.domain.Familia;

import java.util.Optional;

public interface FamiliaRepository {
    Optional<Familia> findById(Long id);
    Optional<Familia> findByUsuarioId(Long usuarioId);
    Familia insert(Familia familia);
    Familia update(Familia familia);
}
