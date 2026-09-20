package com.cuidar.api.notifications.service;

import com.cuidar.api.common.error.ResourceNotFoundException;
import com.cuidar.api.common.web.PageResponse;
import com.cuidar.api.notifications.domain.Notificacion;
import com.cuidar.api.notifications.repository.NotificacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificacionServiceTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @InjectMocks
    private NotificacionService service;

    private Notificacion n1;
    private Notificacion n2;

    @BeforeEach
    void setUp() {
        n1 = new Notificacion(
                1L, 42L, "RESERVA_CONFIRMADA", "Reserva confirmada", "Tu reserva...", null,
                false, null, java.time.OffsetDateTime.now().minusMinutes(2)
        );
        n2 = new Notificacion(
                2L, 42L, "PAGO_LIBERADO", "Pago liberado", "Pago OK...", null,
                false, null, java.time.OffsetDateTime.now().minusMinutes(5)
        );
    }

    @Test
    @DisplayName("listarMias: delega al repo con paginación saneada")
    void listarMias_delega() {
        PageResponse<Notificacion> page = PageResponse.of(java.util.List.of(n1, n2), 0, 20, 2);
        when(notificacionRepository.listarPorUsuario(42L, 0, 20)).thenReturn(page);

        PageResponse<Notificacion> result = service.listarMias(42L, 0, 20);

        assertThat(result.content()).hasSize(2);
        assertThat(result.totalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("listarMias: page negativa se clampea a 0")
    void listarMias_pageNegativa() {
        when(notificacionRepository.listarPorUsuario(42L, 0, 20))
                .thenReturn(PageResponse.of(java.util.List.of(), 0, 20, 0));

        service.listarMias(42L, -5, 20);

        verify(notificacionRepository).listarPorUsuario(42L, 0, 20);
    }

    @Test
    @DisplayName("listarMias: size fuera de rango se clipea a [1, 100]")
    void listarMias_sizeFueraDeRango() {
        when(notificacionRepository.listarPorUsuario(42L, 0, 1))
                .thenReturn(PageResponse.of(java.util.List.of(), 0, 1, 0));
        when(notificacionRepository.listarPorUsuario(42L, 0, 100))
                .thenReturn(PageResponse.of(java.util.List.of(), 0, 100, 0));

        service.listarMias(42L, 0, 0);
        service.listarMias(42L, 0, 500);

        verify(notificacionRepository).listarPorUsuario(42L, 0, 1);
        verify(notificacionRepository).listarPorUsuario(42L, 0, 100);
    }

    @Test
    @DisplayName("marcarLeida: noti de otro usuario -> ResourceNotFoundException (no enumeration)")
    void marcarLeida_otroUsuario() {
        Notificacion deOtro = new Notificacion(
                7L, 99L, "X", "y", null, null, false, null, java.time.OffsetDateTime.now()
        );
        when(notificacionRepository.findById(7L)).thenReturn(Optional.of(deOtro));

        assertThatThrownBy(() -> service.marcarLeida(42L, 7L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(notificacionRepository, never()).marcarLeida(any());
    }

    @Test
    @DisplayName("marcarLeida: ya leída -> no-op (devuelve el mismo record)")
    void marcarLeida_yaLeida() {
        Notificacion yaLeida = new Notificacion(
                1L, 42L, "X", "y", null, null, true, java.time.OffsetDateTime.now().minusHours(1),
                java.time.OffsetDateTime.now().minusHours(2)
        );
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(yaLeida));

        Notificacion result = service.marcarLeida(42L, 1L);

        assertThat(result.leido()).isTrue();
        verify(notificacionRepository, never()).marcarLeida(any());
    }

    @Test
    @DisplayName("marcarLeida: delega al repo si está pendiente")
    void marcarLeida_ok() {
        when(notificacionRepository.findById(2L)).thenReturn(Optional.of(n2));
        Notificacion leida = new Notificacion(
                2L, 42L, "X", "y", null, null, true, java.time.OffsetDateTime.now(),
                n2.createdAt()
        );
        when(notificacionRepository.marcarLeida(2L)).thenReturn(leida);

        Notificacion result = service.marcarLeida(42L, 2L);

        assertThat(result.leido()).isTrue();
        verify(notificacionRepository).marcarLeida(eq(2L));
    }

    @Test
    @DisplayName("crear: persiste noti con leido=false")
    void crear_ok() {
        when(notificacionRepository.save(any(Notificacion.class))).thenAnswer(inv -> {
            Notificacion arg = inv.getArgument(0);
            return new Notificacion(
                    99L, arg.usuarioId(), arg.tipo(), arg.titulo(), arg.contenido(),
                    arg.enlace(), arg.leido(), arg.fechaLectura(), arg.createdAt()
            );
        });

        Notificacion result = service.crear(42L, "TIPO", "titulo", "contenido", null);

        assertThat(result.id()).isEqualTo(99L);
        assertThat(result.usuarioId()).isEqualTo(42L);
        assertThat(result.tipo()).isEqualTo("TIPO");
        assertThat(result.leido()).isFalse();
    }
}