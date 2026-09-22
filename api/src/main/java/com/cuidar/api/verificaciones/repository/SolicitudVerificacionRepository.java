package com.cuidar.api.verificaciones.repository;

import com.cuidar.api.verificaciones.domain.SolicitudVerificacion;

import java.util.List;
import java.util.Optional;

public interface SolicitudVerificacionRepository {
    SolicitudVerificacion insert(SolicitudVerificacion solicitud);
    SolicitudVerificacion update(SolicitudVerificacion solicitud);
    Optional<SolicitudVerificacion> findById(Long id);
    List<SolicitudVerificacion> findAllPendientes();
    Optional<SolicitudVerificacion> findLatestByCuidadorId(Long cuidadorId);
}
