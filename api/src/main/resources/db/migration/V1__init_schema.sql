-- =============================================================================
-- V1__init_schema.sql
-- Plataforma CuidAR - Schema inicial completo
--
-- Convenciones:
--   * snake_case en tablas y columnas
--   * PKs BIGSERIAL (decisión académica; puede migrarse a UUID más adelante)
--   * Auditoría: created_at, updated_at, created_by, updated_by en casi todas
--   * Estados como CHECK constraints (no tipos ENUM para portabilidad)
--   * ON DELETE: CASCADE solo en hijos poseídos, RESTRICT en referencias fuertes
--   * Índices en todas las FKs y en columnas de búsqueda frecuente
--
-- Módulos cubiertos (alineado a RF01-RF20 y a los 12 módulos del charter):
--   0. Núcleo de identidades        (auth: usuario, rol, usuario_rol)
--   1. Familia                      (familia, perfil familiar)
--   2. Cuidador                     (cuidador, certificacion, experiencia, disponibilidad, tarifa)
--   3. Verificación                 (solicitud_verificacion, documento_verificacion)
--   4. Adulto mayor                 (adulto_mayor)
--   5. Búsqueda y favoritos         (favorito)
--   6. Reservas                     (reserva, historial_estado_reserva)
--   7. Pagos                        (pago, comision)
--   8. Comunicación / chat          (conversacion, mensaje)
--   9. Seguimiento                  (reporte_diario)
--  10. Reseñas                      (resena)
--  11. Notificaciones               (notificacion)
-- =============================================================================

SET client_min_messages = WARNING;

-- -----------------------------------------------------------------------------
-- Extensión PGMQ (para colas de mensajes asíncronas; ya viene en la imagen)
-- -----------------------------------------------------------------------------
CREATE EXTENSION IF NOT EXISTS pgmq;


-- =============================================================================
-- MÓDULO 0: NÚCLEO DE IDENTIDADES (auth)
-- =============================================================================

CREATE TABLE usuario (
    id              BIGSERIAL PRIMARY KEY,
    email           VARCHAR(255) NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    nombre          VARCHAR(100) NOT NULL,
    apellido        VARCHAR(100) NOT NULL,
    telefono        VARCHAR(20),
    estado          VARCHAR(20) NOT NULL DEFAULT 'ACTIVO'
                    CHECK (estado IN ('ACTIVO', 'INACTIVO', 'BLOQUEADO', 'PENDIENTE_VERIFICACION')),
    email_verificado BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_registro  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    ultimo_acceso   TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_usuario_email UNIQUE (email)
);
CREATE INDEX idx_usuario_estado ON usuario (estado);

CREATE TABLE rol (
    id          BIGSERIAL PRIMARY KEY,
    nombre      VARCHAR(50) NOT NULL,
    descripcion VARCHAR(255),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_rol_nombre UNIQUE (nombre)
);

CREATE TABLE usuario_rol (
    usuario_id  BIGINT NOT NULL,
    rol_id      BIGINT NOT NULL,
    asignado_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (usuario_id, rol_id),
    CONSTRAINT fk_usuario_rol_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id) ON DELETE CASCADE,
    CONSTRAINT fk_usuario_rol_rol     FOREIGN KEY (rol_id)     REFERENCES rol     (id) ON DELETE RESTRICT
);
CREATE INDEX idx_usuario_rol_rol ON usuario_rol (rol_id);


-- =============================================================================
-- MÓDULO 1: FAMILIA
-- =============================================================================

CREATE TABLE familia (
    id                 BIGSERIAL PRIMARY KEY,
    usuario_id         BIGINT NOT NULL,
    direccion          VARCHAR(500),
    preferencias_busqueda JSONB,
    created_at         TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_familia_usuario UNIQUE (usuario_id),
    CONSTRAINT fk_familia_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id) ON DELETE CASCADE
);


-- =============================================================================
-- MÓDULO 2: CUIDADOR
-- =============================================================================

CREATE TABLE cuidador (
    id                  BIGSERIAL PRIMARY KEY,
    usuario_id          BIGINT NOT NULL,
    presentacion        TEXT,
    anos_experiencia    INTEGER NOT NULL DEFAULT 0 CHECK (anos_experiencia >= 0),
    tarifa_referencial  NUMERIC(10, 2),
    estado_verificacion VARCHAR(30) NOT NULL DEFAULT 'NO_VERIFICADO'
                         CHECK (estado_verificacion IN ('NO_VERIFICADO', 'PENDIENTE', 'VERIFICADO', 'RECHAZADO')),
    estado_publicacion  VARCHAR(20) NOT NULL DEFAULT 'BORRADOR'
                         CHECK (estado_publicacion IN ('BORRADOR', 'PUBLICADO', 'PAUSADO', 'RETIRADO')),
    calificacion_promedio NUMERIC(3, 2) CHECK (calificacion_promedio BETWEEN 0 AND 5),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_cuidador_usuario UNIQUE (usuario_id),
    CONSTRAINT fk_cuidador_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id) ON DELETE CASCADE,
    CONSTRAINT ck_cuidador_tarifa   CHECK (tarifa_referencial IS NULL OR tarifa_referencial >= 0)
);
CREATE INDEX idx_cuidador_estado_verificacion ON cuidador (estado_verificacion);
CREATE INDEX idx_cuidador_estado_publicacion  ON cuidador (estado_publicacion);

CREATE TABLE certificacion (
    id                 BIGSERIAL PRIMARY KEY,
    cuidador_id        BIGINT NOT NULL,
    nombre             VARCHAR(200) NOT NULL,
    institucion        VARCHAR(200),
    fecha_obtencion    DATE,
    fecha_vencimiento  DATE,
    archivo_url        VARCHAR(500),
    created_at         TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_certificacion_cuidador FOREIGN KEY (cuidador_id) REFERENCES cuidador (id) ON DELETE CASCADE,
    CONSTRAINT ck_certificacion_fecha    CHECK (fecha_vencimiento IS NULL OR fecha_vencimiento >= fecha_obtencion)
);
CREATE INDEX idx_certificacion_cuidador ON certificacion (cuidador_id);

CREATE TABLE experiencia_laboral (
    id           BIGSERIAL PRIMARY KEY,
    cuidador_id  BIGINT NOT NULL,
    empresa      VARCHAR(200),
    cargo        VARCHAR(200),
    fecha_inicio DATE,
    fecha_fin    DATE,
    descripcion  TEXT,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_experiencia_cuidador FOREIGN KEY (cuidador_id) REFERENCES cuidador (id) ON DELETE CASCADE,
    CONSTRAINT ck_experiencia_fechas    CHECK (fecha_fin IS NULL OR fecha_fin >= fecha_inicio)
);
CREATE INDEX idx_experiencia_cuidador ON experiencia_laboral (cuidador_id);

CREATE TABLE disponibilidad (
    id              BIGSERIAL PRIMARY KEY,
    cuidador_id     BIGINT NOT NULL,
    dia_semana      SMALLINT NOT NULL CHECK (dia_semana BETWEEN 1 AND 7), -- 1=lunes ... 7=domingo
    hora_inicio     TIME NOT NULL,
    hora_fin        TIME NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_disponibilidad_cuidador FOREIGN KEY (cuidador_id) REFERENCES cuidador (id) ON DELETE CASCADE,
    CONSTRAINT ck_disponibilidad_horas    CHECK (hora_fin > hora_inicio)
);
CREATE INDEX idx_disponibilidad_cuidador ON disponibilidad (cuidador_id);

CREATE TABLE tarifa (
    id           BIGSERIAL PRIMARY KEY,
    cuidador_id  BIGINT NOT NULL,
    modalidad    VARCHAR(20) NOT NULL CHECK (modalidad IN ('POR_HORA', 'POR_DIA', 'POR_MES', 'POR_VISITA')),
    monto        NUMERIC(10, 2) NOT NULL CHECK (monto >= 0),
    fecha_vigencia_desde DATE NOT NULL DEFAULT CURRENT_DATE,
    fecha_vigencia_hasta DATE,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_tarifa_cuidador FOREIGN KEY (cuidador_id) REFERENCES cuidador (id) ON DELETE CASCADE,
    CONSTRAINT ck_tarifa_vigencia  CHECK (fecha_vigencia_hasta IS NULL OR fecha_vigencia_hasta >= fecha_vigencia_desde)
);
CREATE INDEX idx_tarifa_cuidador ON tarifa (cuidador_id);


-- =============================================================================
-- MÓDULO 3: VERIFICACIÓN
-- =============================================================================

CREATE TABLE solicitud_verificacion (
    id              BIGSERIAL PRIMARY KEY,
    cuidador_id     BIGINT NOT NULL,
    fecha_solicitud TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    fecha_resolucion TIMESTAMPTZ,
    estado          VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'
                    CHECK (estado IN ('PENDIENTE', 'EN_REVISION', 'APROBADA', 'RECHAZADA')),
    observaciones   TEXT,
    revisada_por    BIGINT,  -- admin (usuario.id)
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_solicitud_cuidador  FOREIGN KEY (cuidador_id)  REFERENCES cuidador (id) ON DELETE CASCADE,
    CONSTRAINT fk_solicitud_revisor   FOREIGN KEY (revisada_por) REFERENCES usuario  (id) ON DELETE SET NULL
);
CREATE INDEX idx_solicitud_cuidador ON solicitud_verificacion (cuidador_id);
CREATE INDEX idx_solicitud_estado   ON solicitud_verificacion (estado);

CREATE TABLE documento_verificacion (
    id              BIGSERIAL PRIMARY KEY,
    solicitud_id    BIGINT NOT NULL,
    tipo_documento  VARCHAR(50) NOT NULL CHECK (tipo_documento IN ('DNI', 'PASAPORTE', 'ANTECEDENTES_PENALES', 'CERTIFICADO_SALUD', 'DIPLOMA', 'OTRO')),
    archivo_url     VARCHAR(500) NOT NULL,
    estado          VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'
                    CHECK (estado IN ('PENDIENTE', 'APROBADO', 'RECHAZADO')),
    observaciones   TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_documento_solicitud FOREIGN KEY (solicitud_id) REFERENCES solicitud_verificacion (id) ON DELETE CASCADE
);
CREATE INDEX idx_documento_solicitud ON documento_verificacion (solicitud_id);


-- =============================================================================
-- MÓDULO 4: ADULTO MAYOR (la persona a cuidar)
-- =============================================================================

CREATE TABLE adulto_mayor (
    id               BIGSERIAL PRIMARY KEY,
    familia_id       BIGINT NOT NULL,
    nombre           VARCHAR(100) NOT NULL,
    apellido         VARCHAR(100) NOT NULL,
    fecha_nacimiento DATE,
    genero           VARCHAR(20) CHECK (genero IN ('MASCULINO', 'FEMENINO', 'OTRO', 'PREFIERE_NO_DECIR')),
    condicion_salud  TEXT,
    notas            TEXT,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_adulto_mayor_familia FOREIGN KEY (familia_id) REFERENCES familia (id) ON DELETE CASCADE
);
CREATE INDEX idx_adulto_mayor_familia ON adulto_mayor (familia_id);


-- =============================================================================
-- MÓDULO 5: BÚSQUEDA Y FAVORITOS
-- =============================================================================

CREATE TABLE favorito (
    id           BIGSERIAL PRIMARY KEY,
    familia_id   BIGINT NOT NULL,
    cuidador_id  BIGINT NOT NULL,
    fecha_agregado TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_favorito_familia_cuidador UNIQUE (familia_id, cuidador_id),
    CONSTRAINT fk_favorito_familia  FOREIGN KEY (familia_id)  REFERENCES familia  (id) ON DELETE CASCADE,
    CONSTRAINT fk_favorito_cuidador FOREIGN KEY (cuidador_id) REFERENCES cuidador (id) ON DELETE CASCADE
);
CREATE INDEX idx_favorito_cuidador ON favorito (cuidador_id);


-- =============================================================================
-- MÓDULO 6: RESERVAS
-- =============================================================================

CREATE TABLE reserva (
    id                  BIGSERIAL PRIMARY KEY,
    familia_id          BIGINT NOT NULL,
    cuidador_id         BIGINT NOT NULL,
    adulto_mayor_id     BIGINT NOT NULL,
    fecha_inicio        DATE NOT NULL,
    fecha_fin           DATE NOT NULL,
    modalidad           VARCHAR(20) NOT NULL CHECK (modalidad IN ('POR_HORA', 'POR_DIA', 'POR_MES', 'POR_VISITA')),
    horas_estimadas     NUMERIC(6, 2),
    monto_total         NUMERIC(10, 2) NOT NULL CHECK (monto_total >= 0),
    estado              VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'
                        CHECK (estado IN ('PENDIENTE', 'CONFIRMADA', 'EN_CURSO', 'COMPLETADA', 'CANCELADA', 'RECHAZADA')),
    motivo_cancelacion  TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_reserva_familia      FOREIGN KEY (familia_id)      REFERENCES familia      (id) ON DELETE RESTRICT,
    CONSTRAINT fk_reserva_cuidador     FOREIGN KEY (cuidador_id)     REFERENCES cuidador     (id) ON DELETE RESTRICT,
    CONSTRAINT fk_reserva_adulto_mayor FOREIGN KEY (adulto_mayor_id) REFERENCES adulto_mayor (id) ON DELETE RESTRICT,
    CONSTRAINT ck_reserva_fechas       CHECK (fecha_fin >= fecha_inicio)
);
CREATE INDEX idx_reserva_familia   ON reserva (familia_id);
CREATE INDEX idx_reserva_cuidador  ON reserva (cuidador_id);
CREATE INDEX idx_reserva_estado    ON reserva (estado);
CREATE INDEX idx_reserva_fechas    ON reserva (fecha_inicio, fecha_fin);

CREATE TABLE historial_estado_reserva (
    id          BIGSERIAL PRIMARY KEY,
    reserva_id  BIGINT NOT NULL,
    estado_anterior  VARCHAR(20),
    estado_nuevo     VARCHAR(20) NOT NULL,
    cambiado_por     BIGINT,  -- usuario.id
    motivo           TEXT,
    fecha_cambio     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_historial_reserva  FOREIGN KEY (reserva_id)  REFERENCES reserva  (id) ON DELETE CASCADE,
    CONSTRAINT fk_historial_usuario  FOREIGN KEY (cambiado_por) REFERENCES usuario (id) ON DELETE SET NULL
);
CREATE INDEX idx_historial_reserva ON historial_estado_reserva (reserva_id);


-- =============================================================================
-- MÓDULO 7: PAGOS
-- =============================================================================

CREATE TABLE pago (
    id                  BIGSERIAL PRIMARY KEY,
    reserva_id          BIGINT NOT NULL,
    monto               NUMERIC(10, 2) NOT NULL CHECK (monto >= 0),
    comision_plataforma NUMERIC(10, 2) NOT NULL DEFAULT 0 CHECK (comision_plataforma >= 0),
    monto_cuidador      NUMERIC(10, 2) NOT NULL CHECK (monto_cuidador >= 0),
    metodo              VARCHAR(30) CHECK (metodo IN ('TARJETA', 'YAPE', 'PLIN', 'TRANSFERENCIA', 'EFECTIVO')),
    estado              VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'
                        CHECK (estado IN ('PENDIENTE', 'PROCESANDO', 'COMPLETADO', 'FALLIDO', 'REEMBOLSADO')),
    referencia_externa  VARCHAR(200),
    fecha_pago          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    fecha_liberacion    TIMESTAMPTZ,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_pago_reserva FOREIGN KEY (reserva_id) REFERENCES reserva (id) ON DELETE RESTRICT,
    CONSTRAINT ck_pago_montos  CHECK (monto_cuidador + comision_plataforma = monto)
);
CREATE INDEX idx_pago_reserva ON pago (reserva_id);
CREATE INDEX idx_pago_estado  ON pago (estado);


-- =============================================================================
-- MÓDULO 8: COMUNICACIÓN / CHAT
-- =============================================================================

CREATE TABLE conversacion (
    id            BIGSERIAL PRIMARY KEY,
    familia_id    BIGINT NOT NULL,
    cuidador_id   BIGINT NOT NULL,
    fecha_creacion TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    ultimo_mensaje_at TIMESTAMPTZ,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_conversacion_familia_cuidador UNIQUE (familia_id, cuidador_id),
    CONSTRAINT fk_conversacion_familia  FOREIGN KEY (familia_id)  REFERENCES familia  (id) ON DELETE CASCADE,
    CONSTRAINT fk_conversacion_cuidador FOREIGN KEY (cuidador_id) REFERENCES cuidador (id) ON DELETE CASCADE
);

CREATE TABLE mensaje (
    id              BIGSERIAL PRIMARY KEY,
    conversacion_id BIGINT NOT NULL,
    emisor_id       BIGINT NOT NULL,  -- usuario.id (familia o cuidador)
    contenido       TEXT NOT NULL,
    fecha_envio     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    leido           BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_lectura   TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_mensaje_conversacion FOREIGN KEY (conversacion_id) REFERENCES conversacion (id) ON DELETE CASCADE,
    CONSTRAINT fk_mensaje_emisor       FOREIGN KEY (emisor_id)       REFERENCES usuario      (id) ON DELETE RESTRICT
);
CREATE INDEX idx_mensaje_conversacion ON mensaje (conversacion_id, fecha_envio);
CREATE INDEX idx_mensaje_emisor       ON mensaje (emisor_id);


-- =============================================================================
-- MÓDULO 9: SEGUIMIENTO
-- =============================================================================

CREATE TABLE reporte_diario (
    id              BIGSERIAL PRIMARY KEY,
    reserva_id      BIGINT NOT NULL,
    fecha           DATE NOT NULL,
    hora_inicio     TIME,
    hora_fin        TIME,
    actividades     TEXT,
    observaciones   TEXT,
    estado_animo    VARCHAR(20) CHECK (estado_animo IN ('EXCELENTE', 'BUENO', 'REGULAR', 'MALO')),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_reporte_reserva_fecha UNIQUE (reserva_id, fecha),
    CONSTRAINT fk_reporte_reserva FOREIGN KEY (reserva_id) REFERENCES reserva (id) ON DELETE CASCADE,
    CONSTRAINT ck_reporte_horas   CHECK (hora_fin IS NULL OR hora_fin >= hora_inicio)
);
CREATE INDEX idx_reporte_reserva ON reporte_diario (reserva_id);


-- =============================================================================
-- MÓDULO 10: RESEÑAS
-- =============================================================================

CREATE TABLE resena (
    id              BIGSERIAL PRIMARY KEY,
    reserva_id      BIGINT NOT NULL,
    autor_id        BIGINT NOT NULL,  -- usuario.id
    destinatario_id BIGINT NOT NULL,  -- usuario.id
    calificacion    SMALLINT NOT NULL CHECK (calificacion BETWEEN 1 AND 5),
    comentario      TEXT,
    fecha           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_resena_reserva_autor UNIQUE (reserva_id, autor_id),
    CONSTRAINT fk_resena_reserva      FOREIGN KEY (reserva_id)      REFERENCES reserva  (id) ON DELETE CASCADE,
    CONSTRAINT fk_resena_autor        FOREIGN KEY (autor_id)        REFERENCES usuario (id) ON DELETE RESTRICT,
    CONSTRAINT fk_resena_destinatario FOREIGN KEY (destinatario_id) REFERENCES usuario (id) ON DELETE RESTRICT,
    CONSTRAINT ck_resena_distintos    CHECK (autor_id <> destinatario_id)
);
CREATE INDEX idx_resena_destinatario ON resena (destinatario_id);


-- =============================================================================
-- MÓDULO 11: NOTIFICACIONES
-- =============================================================================

CREATE TABLE notificacion (
    id              BIGSERIAL PRIMARY KEY,
    usuario_id      BIGINT NOT NULL,
    tipo            VARCHAR(50) NOT NULL,
    titulo          VARCHAR(200) NOT NULL,
    contenido       TEXT,
    enlace          VARCHAR(500),
    leido           BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_lectura   TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_notificacion_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id) ON DELETE CASCADE
);
CREATE INDEX idx_notificacion_usuario ON notificacion (usuario_id, leido, created_at);


-- =============================================================================
-- TRIGGER: updated_at automático en todas las tablas que lo tengan
-- =============================================================================

CREATE OR REPLACE FUNCTION fn_set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DO $$
DECLARE
    t TEXT;
BEGIN
    FOREACH t IN ARRAY ARRAY[
        'usuario', 'rol', 'familia', 'cuidador',
        'certificacion', 'experiencia_laboral', 'disponibilidad', 'tarifa',
        'solicitud_verificacion', 'documento_verificacion', 'adulto_mayor',
        'reserva', 'historial_estado_reserva', 'pago',
        'conversacion', 'mensaje', 'reporte_diario', 'resena'
    ]
    LOOP
        EXECUTE format(
            'CREATE TRIGGER trg_%s_updated_at BEFORE UPDATE ON %I '
            'FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();',
            t, t
        );
    END LOOP;
END $$;
