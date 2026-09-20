package com.cuidar.api.notifications.domain;

import java.time.OffsetDateTime;

/**
 * Pure-Java domain model for a user-facing notification (RF05).
 *
 * <p>Notification creation is the responsibility of whatever triggered it
 * (booking confirmed, payment released, etc.) — the {@link com.cuidar.api.notifications.service.NotificacionService}
 * provides a {@code crear} method for those internal callers. The HTTP endpoints
 * are read-only.</p>
 *
 * @param id        primary key
 * @param usuarioId the recipient
 * @param tipo      short discriminator (RESERVA_CONFIRMADA, PAGO_LIBERADO, ...)
 * @param titulo    headline for the UI
 * @param contenido optional body
 * @param enlace    optional deep link
 * @param leido     whether the recipient has read it
 * @param fechaLectura when it was first read
 * @param createdAt server-assigned timestamp
 */
public record Notificacion(
        Long id,
        Long usuarioId,
        String tipo,
        String titulo,
        String contenido,
        String enlace,
        boolean leido,
        OffsetDateTime fechaLectura,
        OffsetDateTime createdAt
) {
}