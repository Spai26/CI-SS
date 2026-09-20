package com.cuidar.api.auth.api;

import com.cuidar.api.auth.api.dto.LoginRequest;
import com.cuidar.api.auth.api.dto.LoginResponse;
import com.cuidar.api.auth.api.dto.RegistroRequest;
import com.cuidar.api.auth.api.dto.UpdatePerfilRequest;
import com.cuidar.api.auth.api.dto.UsuarioResponse;
import com.cuidar.api.auth.service.AuthService;
import com.cuidar.api.auth.service.AuthService.RegistroResult;
import com.cuidar.api.auth.service.PerfilService;
import com.cuidar.api.auth.service.security.CurrentUser;
import com.cuidar.api.common.web.ApiPaths;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints for authentication: registration, login, email verification (RF01-03)
 * and current-user profile (RF04).
 *
 * <p>Routes mounted under {@code /api/v1/auth}:</p>
 * <ul>
 *   <li>{@code POST /registro}, {@code POST /login}, {@code POST /verificar-email}: permitAll.</li>
 *   <li>{@code GET /me}, {@code PUT /me}: require JWT.</li>
 * </ul>
 */
@RestController
@RequestMapping(ApiPaths.AUTH)
public class AuthController {

    /** Header exposed only in dev profile (see application-dev.properties + AuthService). */
    public static final String DEV_VERIFICATION_HEADER = "X-Dev-Verification-Token";

    private final AuthService authService;
    private final PerfilService perfilService;
    private final CurrentUser currentUser;

    public AuthController(AuthService authService, PerfilService perfilService, CurrentUser currentUser) {
        this.authService = authService;
        this.perfilService = perfilService;
        this.currentUser = currentUser;
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegistroRequest request) {
        RegistroResult result = authService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(DEV_VERIFICATION_HEADER, result.verificationToken())
                .body(result.usuario());
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/verificar-email")
    public ResponseEntity<UsuarioResponse> verificarEmail(@RequestParam("token") String token) {
        return ResponseEntity.ok(authService.verificarEmail(token));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> me() {
        Long id = currentUser.getCurrentUserId();
        return ResponseEntity.ok(perfilService.obtenerMiPerfil(id));
    }

    @PutMapping("/me")
    public ResponseEntity<UsuarioResponse> updateMe(@Valid @RequestBody UpdatePerfilRequest req) {
        Long id = currentUser.getCurrentUserId();
        return ResponseEntity.ok(perfilService.actualizarMiPerfil(id, req));
    }
}