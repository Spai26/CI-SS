-- =============================================================================
-- V3__fix_usuario_estado_length.sql
-- Ampliar la columna estado para permitir 'PENDIENTE_VERIFICACION' (22 chars)
-- =============================================================================

ALTER TABLE usuario ALTER COLUMN estado TYPE VARCHAR(30);
