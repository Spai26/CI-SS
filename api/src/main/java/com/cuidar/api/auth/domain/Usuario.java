package com.cuidar.api.auth.domain;

import java.time.OffsetDateTime;

/**
 * Pure-Java domain model for a platform user (Familia / Cuidador / Admin).
 *
 * <p>This is NOT a jOOQ record — it is decoupled from persistence so services
 * stay portable, testable, and free of jOOQ dependencies. The
 * {@code UsuarioJooqRepository} translates to/from {@code UsuarioRecord}.</p>
 *
 * @param id              primary key (null until persisted)
 * @param email           unique email used as login
 * @param passwordHash    BCrypt-hashed password (never store the plain password)
 * @param nombre          first name
 * @param apellido        last name
 * @param telefono        optional phone, E.164 format preferred
 * @param estado          one of ACTIVO / INACTIVO / BLOQUEADO / PENDIENTE_VERIFICACION
 * @param emailVerificado whether the email has been verified
 * @param fechaRegistro   timestamp the user signed up (set by the DB)
 */
public record Usuario(
        Long id,
        String email,
        String passwordHash,
        String nombre,
        String apellido,
        String telefono,
        String estado,
        boolean emailVerificado,
        OffsetDateTime fechaRegistro
) {

    public static final String ESTADO_ACTIVO = "ACTIVO";
    public static final String ESTADO_INACTIVO = "INACTIVO";
    public static final String ESTADO_BLOQUEADO = "BLOQUEADO";
    public static final String ESTADO_PENDIENTE_VERIFICACION = "PENDIENTE_VERIFICACION";
}