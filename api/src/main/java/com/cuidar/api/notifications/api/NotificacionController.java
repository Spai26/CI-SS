package com.cuidar.api.notifications.api;

import com.cuidar.api.auth.service.security.CurrentUser;
import com.cuidar.api.common.web.ApiPaths;
import com.cuidar.api.common.web.PageResponse;
import com.cuidar.api.notifications.domain.Notificacion;
import com.cuidar.api.notifications.service.NotificacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints for the recipient-side of the notification module (RF05):
 * list own notifications and mark them as read.
 *
 * <p>Creating notifications is intentionally NOT exposed here — those happen
 * from other modules (reservas, pagos, etc.) calling
 * {@code NotificacionService.crear(...)} directly.</p>
 */
@RestController
@RequestMapping(ApiPaths.V1 + "/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;
    private final CurrentUser currentUser;

    public NotificacionController(NotificacionService notificacionService, CurrentUser currentUser) {
        this.notificacionService = notificacionService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public ResponseEntity<PageResponse<Notificacion>> listarMias(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Long userId = currentUser.getCurrentUserId();
        return ResponseEntity.ok(notificacionService.listarMias(userId, page, size));
    }

    @PostMapping("/{id}/leido")
    public ResponseEntity<Notificacion> marcarLeida(@PathVariable("id") Long id) {
        Long userId = currentUser.getCurrentUserId();
        return ResponseEntity.ok(notificacionService.marcarLeida(userId, id));
    }
}