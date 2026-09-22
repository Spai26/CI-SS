package com.cuidar.api.cuidadores.repository;

import com.cuidar.api.cuidadores.domain.ExperienciaLaboral;

import java.util.List;
import java.util.Optional;

public interface ExperienciaLaboralRepository {
    Optional<ExperienciaLaboral> findById(Long id);
    List<ExperienciaLaboral> findByCuidadorId(Long cuidadorId);
    ExperienciaLaboral insert(ExperienciaLaboral experiencia);
    void deleteById(Long id);
}
