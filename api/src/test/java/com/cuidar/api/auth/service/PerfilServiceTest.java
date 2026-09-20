package com.cuidar.api.auth.service;

import com.cuidar.api.auth.api.dto.UpdatePerfilRequest;
import com.cuidar.api.auth.api.dto.UsuarioResponse;
import com.cuidar.api.auth.domain.Usuario;
import com.cuidar.api.auth.repository.UsuarioRepository;
import com.cuidar.api.common.error.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerfilServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private PerfilService perfilService;

    private Usuario usuarioExistente;

    @BeforeEach
    void setUp() {
        usuarioExistente = new Usuario(
                42L, "[email protected]", "$2a$10$hashed",
                "Maria", "Lopez", "+51999999999",
                Usuario.ESTADO_ACTIVO, true,
                OffsetDateTime.now()
        );
    }

    @Test
    @DisplayName("obtenerMiPerfil: dado userId existente, devuelve UsuarioResponse")
    void obtenerMiPerfil_ok() {
        when(usuarioRepository.findById(42L)).thenReturn(Optional.of(usuarioExistente));

        UsuarioResponse response = perfilService.obtenerMiPerfil(42L);

        assertThat(response.id()).isEqualTo(42L);
        assertThat(response.email()).isEqualTo("[email protected]");
        assertThat(response.estado()).isEqualTo("ACTIVO");
    }

    @Test
    @DisplayName("obtenerMiPerfil: userId inexistente -> ResourceNotFoundException")
    void obtenerMiPerfil_inexistente() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> perfilService.obtenerMiPerfil(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("actualizarMiPerfil: updatea solo nombre/apellido/telefono, conserva resto")
    void actualizarMiPerfil_ok() {
        when(usuarioRepository.findById(42L)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.update(any())).thenAnswer(inv -> inv.getArgument(0));

        UpdatePerfilRequest req = new UpdatePerfilRequest("Ana", "Pérez", "+51888888888");

        UsuarioResponse response = perfilService.actualizarMiPerfil(42L, req);

        assertThat(response.id()).isEqualTo(42L);
        assertThat(response.email()).isEqualTo("[email protected]"); // inmutable
        assertThat(response.nombre()).isEqualTo("Ana");
        assertThat(response.apellido()).isEqualTo("Pérez");
        assertThat(response.telefono()).isEqualTo("+51888888888");

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).update(captor.capture());
        // inmunes: email, passwordHash, estado, emailVerificado, fechaRegistro
        assertThat(captor.getValue().email()).isEqualTo("[email protected]");
        assertThat(captor.getValue().passwordHash()).isEqualTo("$2a$10$hashed");
        assertThat(captor.getValue().estado()).isEqualTo("ACTIVO");
        assertThat(captor.getValue().emailVerificado()).isTrue();
    }

    @Test
    @DisplayName("actualizarMiPerfil: userId inexistente -> ResourceNotFoundException")
    void actualizarMiPerfil_inexistente() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> perfilService.actualizarMiPerfil(99L, new UpdatePerfilRequest("x", "y", "+5111")))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}