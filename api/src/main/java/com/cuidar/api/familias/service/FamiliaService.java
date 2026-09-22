package com.cuidar.api.familias.service;

import com.cuidar.api.familias.api.dto.ActualizarFamiliaRequest;
import com.cuidar.api.familias.api.dto.AdultoMayorRequest;
import com.cuidar.api.familias.domain.AdultoMayor;
import com.cuidar.api.familias.domain.Familia;
import com.cuidar.api.familias.repository.AdultoMayorRepository;
import com.cuidar.api.familias.repository.FamiliaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FamiliaService {

    private final FamiliaRepository familiaRepository;
    private final AdultoMayorRepository adultoMayorRepository;

    public FamiliaService(FamiliaRepository familiaRepository, AdultoMayorRepository adultoMayorRepository) {
        this.familiaRepository = familiaRepository;
        this.adultoMayorRepository = adultoMayorRepository;
    }

    @Transactional(readOnly = true)
    public Familia getFamiliaByUsuarioId(Long usuarioId) {
        return familiaRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    // Si no existe, creamos el perfil vacío de la familia
                    Familia nueva = new Familia(null, usuarioId, null, null, null, null);
                    return familiaRepository.insert(nueva);
                });
    }

    @Transactional
    public Familia actualizarFamilia(Long usuarioId, ActualizarFamiliaRequest request) {
        Familia familia = getFamiliaByUsuarioId(usuarioId);
        Familia actualizada = new Familia(
                familia.id(),
                familia.usuarioId(),
                request.direccion(),
                request.preferenciasBusqueda(),
                familia.createdAt(),
                familia.updatedAt()
        );
        return familiaRepository.update(actualizada);
    }

    @Transactional(readOnly = true)
    public List<AdultoMayor> listarAdultosMayores(Long usuarioId) {
        Familia familia = getFamiliaByUsuarioId(usuarioId);
        return adultoMayorRepository.findByFamiliaId(familia.id());
    }

    @Transactional
    public AdultoMayor agregarAdultoMayor(Long usuarioId, AdultoMayorRequest request) {
        Familia familia = getFamiliaByUsuarioId(usuarioId);
        AdultoMayor adulto = new AdultoMayor(
                null,
                familia.id(),
                request.nombre(),
                request.apellido(),
                request.fechaNacimiento(),
                request.genero(),
                request.condicionSalud(),
                request.notas(),
                null,
                null
        );
        return adultoMayorRepository.insert(adulto);
    }
}
