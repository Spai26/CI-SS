package com.cuidar.api.notifications.repository;

import com.cuidar.api.common.web.PageResponse;
import com.cuidar.api.notifications.domain.Notificacion;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * In-memory implementation of {@link NotificacionRepository} for the MVP.
 *
 * <p>Persists notifications in a per-user {@code List}. Replace with a jOOQ
 * implementation once we wire the {@code notificacion} table to the service —
 * for now, this lets the controller / service layer be exercised via HTTP
 * without a live DB.</p>
 */
@Repository
public class InMemoryNotificacionRepository implements NotificacionRepository {

    private final java.util.concurrent.ConcurrentHashMap<Long, Notificacion> store = new java.util.concurrent.ConcurrentHashMap<>();
    private final java.util.concurrent.atomic.AtomicLong seq = new java.util.concurrent.atomic.AtomicLong(0);

    private static final com.cuidar.api.jooq.generated.tables.Notificacion N =
            com.cuidar.api.jooq.generated.tables.Notificacion.NOTIFICACION;

    @Override
    public PageResponse<Notificacion> listarPorUsuario(Long usuarioId, int page, int size) {
        List<Notificacion> all = store.values().stream()
                .filter(n -> n.usuarioId().equals(usuarioId))
                .sorted((a, b) -> b.createdAt().compareTo(a.createdAt()))
                .toList();
        long total = all.size();
        int from = Math.min(page * size, all.size());
        int to = Math.min(from + size, all.size());
        List<Notificacion> slice = all.subList(from, to);
        return PageResponse.of(slice, page, size, total);
    }

    @Override
    public Optional<Notificacion> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Notificacion marcarLeida(Long id) {
        Notificacion prev = store.get(id);
        if (prev == null) {
            throw new IllegalStateException("Notificacion no encontrada: id=" + id);
        }
        Notificacion next = new Notificacion(
                prev.id(), prev.usuarioId(), prev.tipo(), prev.titulo(),
                prev.contenido(), prev.enlace(),
                true, java.time.OffsetDateTime.now(), prev.createdAt()
        );
        store.put(id, next);
        return next;
    }

    @Override
    public Notificacion save(Notificacion n) {
        Long id = n.id() != null ? n.id() : seq.incrementAndGet();
        Notificacion toStore = new Notificacion(
                id, n.usuarioId(), n.tipo(), n.titulo(), n.contenido(), n.enlace(),
                n.leido(), n.fechaLectura(),
                n.createdAt() != null ? n.createdAt() : java.time.OffsetDateTime.now()
        );
        store.put(id, toStore);
        return toStore;
    }
}