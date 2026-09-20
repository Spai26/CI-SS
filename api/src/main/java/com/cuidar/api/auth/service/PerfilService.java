package com.cuidar.api.auth.service;

import com.cuidar.api.auth.api.dto.UpdatePerfilRequest;
import com.cuidar.api.auth.api.dto.UsuarioResponse;
import com.cuidar.api.auth.domain.Usuario;
import com.cuidar.api.auth.repository.UsuarioRepository;
import com.cuidar.api.common.error.ResourceNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Profile use-cases (RF04): obtener y actualizar el perfil del usuario
 * autenticado. Recibe el userId por parámetro para mantener el service
 * libre de dependencias de Spring Security (testeable con mocks).
 */
@Service
public class PerfilService {

    private final UsuarioRepository usuarioRepository;

    public PerfilService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioResponse obtenerMiPerfil(Long userId) {
        Usuario u = usuarioRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.of("Usuario", userId));
        return toResponse(u);
    }

    public UsuarioResponse actualizarMiPerfil(Long userId, UpdatePerfilRequest req) {
        Usuario actual = usuarioRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.of("Usuario", userId));
        Usuario actualizado = new Usuario(
                actual.id(),
                actual.email(),
                actual.passwordHash(),
                req.nombre().trim(),
                req.apellido().trim(),
                req.telefono(),
                actual.estado(),
                actual.emailVerificado(),
                actual.fechaRegistro()
        );
        Usuario guardado = usuarioRepository.update(actualizado);
        return toResponse(guardado);
    }

    private UsuarioResponse toResponse(Usuario u) {
        return new UsuarioResponse(
                u.id(),
                u.email(),
                u.nombre(),
                u.apellido(),
                u.telefono(),
                u.estado(),
                u.emailVerificado()
        );
    }
}