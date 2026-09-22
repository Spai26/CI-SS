package com.cuidar.api.familias.repository;

import com.cuidar.api.familias.domain.AdultoMayor;

import java.util.List;
import java.util.Optional;

public interface AdultoMayorRepository {
    Optional<AdultoMayor> findById(Long id);
    List<AdultoMayor> findByFamiliaId(Long familiaId);
    AdultoMayor insert(AdultoMayor adultoMayor);
    AdultoMayor update(AdultoMayor adultoMayor);
    void deleteById(Long id);
}
