package com.cuidar.api.auth.service;

import com.cuidar.api.auth.api.dto.LoginRequest;
import com.cuidar.api.auth.api.dto.LoginResponse;
import com.cuidar.api.auth.api.dto.RegistroRequest;
import com.cuidar.api.auth.api.dto.UsuarioResponse;
import com.cuidar.api.auth.domain.Usuario;
import com.cuidar.api.auth.repository.EmailVerificationTokenRepository;
import com.cuidar.api.auth.repository.EmailVerificationTokenRepository.EmailVerificationToken;
import com.cuidar.api.auth.repository.UsuarioRepository;
import com.cuidar.api.auth.service.security.JwtService;
import com.cuidar.api.common.error.BusinessRuleException;
import com.cuidar.api.common.error.CredencialesInvalidasException;
import com.cuidar.api.common.error.EmailYaRegistradoException;
import com.cuidar.api.common.error.TokenInvalidoException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Optional;

/**
 * Authentication use-cases: registro (RF01), login (RF02), verificación de email
 * (RF03). Pure application logic; depends only on repositories, password encoder
 * and jwt service.
 */
@Service
public class AuthService {

    private static final SecureRandom RNG = new SecureRandom();
    private static final long TOKEN_EXPIRATION_HOURS = 24L;

    private final UsuarioRepository usuarioRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            EmailVerificationTokenRepository tokenRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Registra un nuevo usuario (RF01) y crea su token de verificación.
     *
     * <p>El token NO se devuelve en el response body (se considera secreto). En
     * profile dev el controller lo expone en la cabecera
     * {@code X-Dev-Verification-Token} para que el flujo end-to-end se pueda
     * probar sin SMTP; en prod esa cabecera se omite y el token viaja por
     * email.</p>
     */
    public RegistroResult registrar(RegistroRequest request) {
        if (request == null) {
            throw new BusinessRuleException("El request de registro no puede ser nulo");
        }

        String email = request.email().trim().toLowerCase();
        if (usuarioRepository.existsByEmail(email)) {
            throw new EmailYaRegistradoException(email);
        }

        String passwordHash = passwordEncoder.encode(request.password());
        Usuario aPersistir = new Usuario(
                null,
                email,
                passwordHash,
                request.nombre().trim(),
                request.apellido().trim(),
                request.telefono(),
                Usuario.ESTADO_PENDIENTE_VERIFICACION,
                false,
                null
        );

        Usuario guardado = usuarioRepository.insert(aPersistir);
        EmailVerificationToken token = generarYGuardarToken(guardado.id());

        return new RegistroResult(toResponse(guardado), token.token());
    }

    public LoginResponse login(LoginRequest request) {
        if (request == null) {
            throw new CredencialesInvalidasException();
        }

        Optional<Usuario> opt = usuarioRepository.findByEmail(request.email().trim().toLowerCase());
        if (opt.isEmpty()) {
            throw new CredencialesInvalidasException();
        }

        Usuario usuario = opt.get();

        if (!Usuario.ESTADO_ACTIVO.equals(usuario.estado()) || !usuario.emailVerificado()) {
            throw new CredencialesInvalidasException();
        }

        if (!passwordEncoder.matches(request.password(), usuario.passwordHash())) {
            throw new CredencialesInvalidasException();
        }

        String token = jwtService.generarToken(usuario);
        return new LoginResponse(
                token,
                LoginResponse.BEARER,
                jwtService.expiresInSeconds(),
                toResponse(usuario)
        );
    }

    public UsuarioResponse verificarEmail(String tokenValue) {
        Optional<EmailVerificationToken> opt = tokenRepository.findByToken(tokenValue);
        if (opt.isEmpty() || opt.get().isUsed() || opt.get().isExpired(OffsetDateTime.now())) {
            throw new TokenInvalidoException(tokenValue);
        }
        EmailVerificationToken token = opt.get();

        Optional<Usuario> optUsuario = usuarioRepository.findById(token.usuarioId());
        if (optUsuario.isEmpty()) {
            throw new TokenInvalidoException(tokenValue);
        }

        Usuario usuario = optUsuario.get();
        Usuario activado = usuarioRepository.update(new Usuario(
                usuario.id(),
                usuario.email(),
                usuario.passwordHash(),
                usuario.nombre(),
                usuario.apellido(),
                usuario.telefono(),
                Usuario.ESTADO_ACTIVO,
                true,
                usuario.fechaRegistro()
        ));

        tokenRepository.markUsed(token);
        return toResponse(activado);
    }

    private EmailVerificationToken generarYGuardarToken(Long usuarioId) {
        byte[] bytes = new byte[32];
        RNG.nextBytes(bytes);
        String tokenValue = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        OffsetDateTime ahora = OffsetDateTime.now();
        return tokenRepository.save(new EmailVerificationToken(
                null,
                usuarioId,
                tokenValue,
                ahora,
                ahora.plusHours(TOKEN_EXPIRATION_HOURS),
                null
        ));
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

    /** Result of {@link #registrar(RegistroRequest)}: user public view + raw verification token. */
    public record RegistroResult(UsuarioResponse usuario, String verificationToken) {
    }
}