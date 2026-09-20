-- =============================================================================
-- V2__email_verification.sql
-- Tabla email_verification_token para flujo de verificación de email (RF03).
--
-- No eliminamos este cambio: cuando un usuario se registra, se genera un token
-- único que se persiste acá. El usuario hace click en el link (con el token),
-- y nuestro endpoint /api/v1/auth/verificar-email lo marca como usado y
-- promueve el usuario a estado ACTIVO con emailVerificado=true.
--
-- En dev (sin SMTP) el token se devuelve al cliente en una cabecera de respuesta
-- para que el flujo se pueda probar end-to-end. En prod se omite la cabecera y
-- se envía por email.
-- =============================================================================

CREATE TABLE email_verification_token (
    id                  BIGSERIAL PRIMARY KEY,
    usuario_id          BIGINT NOT NULL,
    token               VARCHAR(128) NOT NULL,
    fecha_creacion      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    fecha_expiracion    TIMESTAMPTZ NOT NULL,
    fecha_uso           TIMESTAMPTZ,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_email_verification_token UNIQUE (token),
    CONSTRAINT fk_email_verification_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario (id) ON DELETE CASCADE
);
CREATE INDEX idx_email_verification_usuario ON email_verification_token (usuario_id);
CREATE INDEX idx_email_verification_token   ON email_verification_token (token);

CREATE TRIGGER trg_email_verification_updated_at
    BEFORE UPDATE ON email_verification_token
    FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();
