package com.cuidar.api.cuidadores.service;

import com.cuidar.api.cuidadores.api.dto.ActualizarCuidadorRequest;
import com.cuidar.api.cuidadores.api.dto.BusquedaCuidadorRequest;
import com.cuidar.api.cuidadores.api.dto.ExperienciaLaboralRequest;
import com.cuidar.api.cuidadores.domain.Cuidador;
import com.cuidar.api.cuidadores.domain.ExperienciaLaboral;
import com.cuidar.api.cuidadores.repository.CuidadorRepository;
import com.cuidar.api.cuidadores.repository.ExperienciaLaboralRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CuidadorService {

    private final CuidadorRepository cuidadorRepository;
    private final ExperienciaLaboralRepository experienciaRepository;

    public CuidadorService(CuidadorRepository cuidadorRepository, ExperienciaLaboralRepository experienciaRepository) {
        this.cuidadorRepository = cuidadorRepository;
        this.experienciaRepository = experienciaRepository;
    }

    @Transactional(readOnly = true)
    public Cuidador getCuidadorByUsuarioId(Long usuarioId) {
        return cuidadorRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    // Si no existe, creamos el perfil vacío del cuidador
                    Cuidador nuevo = new Cuidador(
                            null,
                            usuarioId,
                            null,
                            0,
                            null,
                            Cuidador.ESTADO_VERIFICACION_NO_VERIFICADO,
                            Cuidador.ESTADO_PUBLICACION_BORRADOR,
                            BigDecimal.ZERO,
                            null,
                            null
                    );
                    return cuidadorRepository.insert(nuevo);
                });
    }

    @Transactional
    public Cuidador actualizarCuidador(Long usuarioId, ActualizarCuidadorRequest request) {
        Cuidador cuidador = getCuidadorByUsuarioId(usuarioId);
        Cuidador actualizado = new Cuidador(
                cuidador.id(),
                cuidador.usuarioId(),
                request.presentacion(),
                request.anosExperiencia(),
                request.tarifaReferencial(),
                cuidador.estadoVerificacion(),
                cuidador.estadoPublicacion(),
                cuidador.calificacionPromedio(),
                cuidador.createdAt(),
                cuidador.updatedAt()
        );
        return cuidadorRepository.update(actualizado);
    }

    @Transactional(readOnly = true)
    public List<Cuidador> buscarCuidadores(BusquedaCuidadorRequest filtros) {
        return cuidadorRepository.buscarCuidadores(filtros);
    }

    @Transactional(readOnly = true)
    public List<ExperienciaLaboral> listarExperiencias(Long usuarioId) {
        Cuidador cuidador = getCuidadorByUsuarioId(usuarioId);
        return experienciaRepository.findByCuidadorId(cuidador.id());
    }

    @Transactional
    public ExperienciaLaboral agregarExperiencia(Long usuarioId, ExperienciaLaboralRequest request) {
        Cuidador cuidador = getCuidadorByUsuarioId(usuarioId);
        ExperienciaLaboral experiencia = new ExperienciaLaboral(
                null,
                cuidador.id(),
                request.empresa(),
                request.cargo(),
                request.fechaInicio(),
                request.fechaFin(),
                request.descripcion(),
                null,
                null
        );
        return experienciaRepository.insert(experiencia);
    }
}
