package com.cuidar.api.notifications.service;

import com.cuidar.api.common.error.ResourceNotFoundException;
import com.cuidar.api.common.web.PageResponse;
import com.cuidar.api.notifications.domain.Notificacion;
import com.cuidar.api.notifications.repository.NotificacionRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

/**
 * Use-cases for in-app notifications (RF05).
 *
 * <p>{@link #crear} is for internal callers (other modules); {@link #listarMias}
 * and {@link #marcarLeida} are invoked by the HTTP layer on behalf of the
 * authenticated user.</p>
 */
@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;

    public NotificacionService(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    public PageResponse<Notificacion> listarMias(Long usuarioId, int page, int size) {
        int safePage = Math.max(0, page);
        int safeSize = Math.min(Math.max(size, 1), 100);
        return notificacionRepository.listarPorUsuario(usuarioId, safePage, safeSize);
    }

    public Notificacion marcarLeida(Long usuarioId, Long id) {
        Notificacion n = notificacionRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Notificacion", id));
        if (!n.usuarioId().equals(usuarioId)) {
            // no enumeration: 404 as if it didn't exist
            throw ResourceNotFoundException.of("Notificacion", id);
        }
        if (n.leido()) {
            return n;
        }
        return notificacionRepository.marcarLeida(id);
    }

    /** Internal API for other modules to publish a notification. */
    public Notificacion crear(Long usuarioId, String tipo, String titulo, String contenido, String enlace) {
        return notificacionRepository.save(new Notificacion(
                null, usuarioId, tipo, titulo, contenido, enlace,
                false, null, OffsetDateTime.now()
        ));
    }
}