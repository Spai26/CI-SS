package com.cuidar.api.notifications.repository;

import com.cuidar.api.common.web.PageResponse;
import com.cuidar.api.notifications.domain.Notificacion;

import java.util.Optional;

/**
 * DAO contract for user notifications (RF05).
 */
public interface NotificacionRepository {

    /**
     * Lists the notifications addressed to the given user, newest first.
     */
    PageResponse<Notificacion> listarPorUsuario(Long usuarioId, int page, int size);

    Optional<Notificacion> findById(Long id);

    /**
     * Marks the notification as read. Returns the updated representation.
     */
    Notificacion marcarLeida(Long id);

    /**
     * Internal use: creates a new notification for a user. Not exposed via HTTP.
     */
    Notificacion save(Notificacion notificacion);
}