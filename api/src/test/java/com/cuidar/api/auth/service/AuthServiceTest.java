package com.cuidar.api.auth.service;

import com.cuidar.api.auth.api.dto.LoginRequest;
import com.cuidar.api.auth.api.dto.LoginResponse;
import com.cuidar.api.auth.api.dto.RegistroRequest;
import com.cuidar.api.auth.api.dto.UsuarioResponse;
import com.cuidar.api.auth.domain.Usuario;
import com.cuidar.api.auth.repository.EmailVerificationTokenRepository;
import com.cuidar.api.auth.repository.UsuarioRepository;
import com.cuidar.api.auth.service.security.JwtService;
import com.cuidar.api.common.error.BusinessRuleException;
import com.cuidar.api.common.error.CredencialesInvalidasException;
import com.cuidar.api.common.error.EmailYaRegistradoException;
import com.cuidar.api.common.error.TokenInvalidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EmailVerificationTokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private RegistroRequest registroRequest;

    @BeforeEach
    void setUp() {
        registroRequest = new RegistroRequest(
                "[email protected]",
                "Secreta123!",
                "Maria",
                "Lopez",
                "+51999999999"
        );
    }

    // ========================================================================
    // REGISTRO (RF01)
    // ========================================================================

    @Test
    @DisplayName("registrar: dado email nuevo, persiste + genera token + devuelve el usuario creado")
    void registrar_emailNuevo_persisteYDevuelve() {
        when(usuarioRepository.existsByEmail("[email protected]")).thenReturn(false);
        when(passwordEncoder.encode("Secreta123!")).thenReturn("$2a$10$hashed");
        when(usuarioRepository.insert(any(Usuario.class))).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            // simulate that the DB set the id
            return new Usuario(
                    42L, u.email(), u.passwordHash(), u.nombre(), u.apellido(),
                    u.telefono(), u.estado(), u.emailVerificado(), OffsetDateTime.now()
            );
        });
        when(tokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AuthService.RegistroResult result = authService.registrar(registroRequest);
        UsuarioResponse response = result.usuario();

        assertThat(response.id()).isEqualTo(42L);
        assertThat(response.email()).isEqualTo("[email protected]");
        assertThat(response.estado()).isEqualTo("PENDIENTE_VERIFICACION");
        assertThat(response.emailVerificado()).isFalse();
        assertThat(result.verificationToken()).isNotBlank();

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).insert(captor.capture());
        assertThat(captor.getValue().passwordHash()).isEqualTo("$2a$10$hashed");

        verify(tokenRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("registrar: si el email ya existe, NO persiste y lanza EmailYaRegistradoException")
    void registrar_emailExistente_lanzaExcepcion() {
        when(usuarioRepository.existsByEmail("[email protected]")).thenReturn(true);

        assertThatThrownBy(() -> authService.registrar(registroRequest))
                .isInstanceOf(EmailYaRegistradoException.class)
                .hasMessageContaining("[email protected]");

        verify(usuarioRepository, never()).insert(any());
        verify(passwordEncoder, never()).encode(anyString());
        verify(tokenRepository, never()).save(any());
    }

    @Test
    @DisplayName("registrar: request nulo, lanza BusinessRuleException")
    void registrar_requestNulo_lanzaBusinessRule() {
        assertThatThrownBy(() -> authService.registrar(null))
                .isInstanceOf(BusinessRuleException.class);
    }

    // ========================================================================
    // LOGIN (RF02)
    // ========================================================================

    @Nested
    @DisplayName("login()")
    class LoginTests {

        private LoginRequest loginRequest;
        private Usuario usuarioExistente;

        @BeforeEach
        void setUpLogin() {
            loginRequest = new LoginRequest("[email protected]", "Secreta123!");
            usuarioExistente = new Usuario(
                    42L,
                    "[email protected]",
                    "$2a$10$hashed",
                    "Maria",
                    "Lopez",
                    "+51999999999",
                    Usuario.ESTADO_ACTIVO,
                    true,
                    OffsetDateTime.now()
            );
        }

        @Test
        @DisplayName("login OK: devuelve JWT y datos del usuario")
        void login_ok_devuelveJwt() {
            when(usuarioRepository.findByEmail("[email protected]")).thenReturn(Optional.of(usuarioExistente));
            when(passwordEncoder.matches("Secreta123!", "$2a$10$hashed")).thenReturn(true);
            when(jwtService.generarToken(usuarioExistente)).thenReturn("eyJhbGc.tokenValue");
            when(jwtService.expiresInSeconds()).thenReturn(3600L);

            LoginResponse response = authService.login(loginRequest);

            assertThat(response.accessToken()).isEqualTo("eyJhbGc.tokenValue");
            assertThat(response.tokenType()).isEqualTo("Bearer");
            assertThat(response.expiresInSeconds()).isEqualTo(3600L);
            assertThat(response.usuario().id()).isEqualTo(42L);
        }

        @Test
        @DisplayName("login con email inexistente -> CredencialesInvalidasException")
        void login_emailInexistente_lanzaCredencialesInvalidas() {
            when(usuarioRepository.findByEmail("[email protected]")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.login(loginRequest))
                    .isInstanceOf(CredencialesInvalidasException.class);
        }

        @Test
        @DisplayName("login con password incorrecto -> CredencialesInvalidasException")
        void login_passwordIncorrecto_lanzaCredencialesInvalidas() {
            when(usuarioRepository.findByEmail("[email protected]")).thenReturn(Optional.of(usuarioExistente));
            when(passwordEncoder.matches("Secreta123!", "$2a$10$hashed")).thenReturn(false);

            assertThatThrownBy(() -> authService.login(loginRequest))
                    .isInstanceOf(CredencialesInvalidasException.class);
        }

        @Test
        @DisplayName("login con usuario no verificado -> CredencialesInvalidasException")
        void login_usuarioNoVerificado_lanzaCredencialesInvalidas() {
            Usuario noVerificado = new Usuario(
                    42L, "[email protected]", "$2a$10$hashed",
                    "Maria", "Lopez", "+51999999999",
                    Usuario.ESTADO_ACTIVO, false, OffsetDateTime.now()
            );
            when(usuarioRepository.findByEmail("[email protected]")).thenReturn(Optional.of(noVerificado));

            assertThatThrownBy(() -> authService.login(loginRequest))
                    .isInstanceOf(CredencialesInvalidasException.class);
        }
    }

    // ========================================================================
    // VERIFICACIÓN DE EMAIL (RF03)
    // ========================================================================

    @Nested
    @DisplayName("verificarEmail()")
    class VerificarEmailTests {

        @Test
        @DisplayName("verificar: token válido + usuario PENDIENTE -> activa usuario y marca token usado")
        void verificar_tokenValido_activaUsuario() {
            Usuario pendiente = new Usuario(
                    42L, "[email protected]", "$2a$10$hash",
                    "Maria", "Lopez", "+51999999999",
                    Usuario.ESTADO_PENDIENTE_VERIFICACION, false,
                    OffsetDateTime.now()
            );
            EmailVerificationTokenRepository.EmailVerificationToken t = new EmailVerificationTokenRepository.EmailVerificationToken(
                    1L, 42L, "valid-token",
                    OffsetDateTime.now().minusMinutes(1),
                    OffsetDateTime.now().plusHours(24),
                    null
            );
            when(tokenRepository.findByToken("valid-token")).thenReturn(Optional.of(t));
            when(usuarioRepository.findById(42L)).thenReturn(Optional.of(pendiente));
            when(usuarioRepository.update(any())).thenAnswer(inv -> inv.getArgument(0));

            UsuarioResponse response = authService.verificarEmail("valid-token");

            assertThat(response.id()).isEqualTo(42L);
            assertThat(response.estado()).isEqualTo("ACTIVO");
            assertThat(response.emailVerificado()).isTrue();

            ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
            verify(usuarioRepository).update(captor.capture());
            assertThat(captor.getValue().estado()).isEqualTo("ACTIVO");
            assertThat(captor.getValue().emailVerificado()).isTrue();

            ArgumentCaptor<EmailVerificationTokenRepository.EmailVerificationToken> tokenCaptor =
                    ArgumentCaptor.forClass(EmailVerificationTokenRepository.EmailVerificationToken.class);
            verify(tokenRepository).markUsed(tokenCaptor.capture());
            assertThat(tokenCaptor.getValue().token()).isEqualTo("valid-token");
            assertThat(tokenCaptor.getValue().usuarioId()).isEqualTo(42L);
        }

        @Test
        @DisplayName("verificar: token expirado -> TokenInvalidoException")
        void verificar_tokenExpirado_lanzaExcepcion() {
            EmailVerificationTokenRepository.EmailVerificationToken t = new EmailVerificationTokenRepository.EmailVerificationToken(
                    1L, 42L, "expired-token",
                    OffsetDateTime.now().minusHours(48),
                    OffsetDateTime.now().minusHours(1),
                    null
            );
            when(tokenRepository.findByToken("expired-token")).thenReturn(Optional.of(t));

            assertThatThrownBy(() -> authService.verificarEmail("expired-token"))
                    .isInstanceOf(TokenInvalidoException.class);

            verify(usuarioRepository, never()).update(any());
            verify(tokenRepository, never()).markUsed(any());
        }

        @Test
        @DisplayName("verificar: token ya usado -> TokenInvalidoException")
        void verificar_tokenUsado_lanzaExcepcion() {
            EmailVerificationTokenRepository.EmailVerificationToken t = new EmailVerificationTokenRepository.EmailVerificationToken(
                    1L, 42L, "used-token",
                    OffsetDateTime.now().minusMinutes(1),
                    OffsetDateTime.now().plusHours(24),
                    OffsetDateTime.now().minusMinutes(1)
            );
            when(tokenRepository.findByToken("used-token")).thenReturn(Optional.of(t));

            assertThatThrownBy(() -> authService.verificarEmail("used-token"))
                    .isInstanceOf(TokenInvalidoException.class);
        }

        @Test
        @DisplayName("verificar: token inexistente -> TokenInvalidoException")
        void verificar_tokenInexistente_lanzaExcepcion() {
            when(tokenRepository.findByToken("nope")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.verificarEmail("nope"))
                    .isInstanceOf(TokenInvalidoException.class);
        }
    }
}