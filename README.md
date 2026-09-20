# CuidAR API

Backend REST para la plataforma **CuidAR** — conecta familias con cuidadores verificados para el adulto mayor.

> Proyecto académico · UTP 2026-2 · Integrador I (Sistemas-Software) + Análisis y Diseño de Sistemas de Información.

---

## Stack

| Capa | Tecnología |
|---|---|
| Lenguaje | Java 21 |
| Framework | Spring Boot 4.1.1 |
| Persistencia | Spring Data JPA + Hibernate |
| **Migrations** | **Flyway** (schema-first — `db/migration/`) |
| Base de datos | PostgreSQL 18 (con extensión [PGMQ](https://github.com/pgmq/pg18-pgmq)) |
| Seguridad | Spring Security + OAuth2 Resource Server (JWT) |
| Validación | Jakarta Bean Validation |
| Build | Maven |
| Testing | JUnit 5 + AssertJ + **Testcontainers** (Postgres real) |
| Frontend | React 19 + Vite + TailwindCSS 4 + React Router |

---

## Prerrequisitos

- **Java 21** (`java -version`)
- **Maven 3.9+** (o usar el `mvnw` incluido)
- **Docker Desktop** corriendo (para Postgres local y para Testcontainers)

---

## Levantar la Infraestructura y el Frontend

Desde la raíz del repo, levanta Postgres, pgAdmin y el Frontend de React:

```bash
docker compose up -d
```

- **Frontend**: Servido en `http://localhost:5173` (con proxy al backend en `localhost:8080`).
- **Postgres**: Expuesto en `localhost:5440` · DB `DBCUIDAR` · user `postgres` · pass `devpw_2025`
- **pgAdmin**: UI en `http://localhost:8083` · `admin@admin.com` / `admin`
- **Migrations**: Las aplica Flyway al arrancar la app.

---

## Correr la API en modo dev

```bash
# desde api/
mvn spring-boot:run
```

o:

```bash
mvn -pl api spring-boot:run
```

- Arranca en `http://localhost:8080`
- Profile activo por defecto: **`dev`**
- Health: `GET /actuator/health`
- Endpoints protegidos: requieren JWT (en dev se acepta cualquier token; ver `SecurityConfig`)

### Variables de entorno relevantes (dev)

| Variable | Default | Uso |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | `dev` | Perfil Spring |
| `SERVER_PORT` | `8080` | Puerto HTTP |
| `DB_USERNAME` | `postgres` | Usuario Postgres |
| `DB_PASSWORD` | `devpw_2025` | Password Postgres |
| `JWT_ISSUER_URI` | `http://localhost:9000` | URL del IdP (placeholder en dev) |

---

## Correr tests

```bash
mvn test
```

Los tests de integración levantan un container real de Postgres (Testcontainers, misma imagen que dev).
El container se reutiliza entre runs (`withReuse(true)`) para acelerar la suite.

---

## Estructura del proyecto

Paquetes organizados por **dominio** (no por capa técnica). Cada módulo sigue MVC clásico internamente, alineado al sílabo:

```
com.cuidar.api/
├── ApiApplication.java        ← entrypoint
├── common/                    ← cross-cutting (errores, seguridad, web base)
│   ├── error/                 ← ApiError (RFC 7807), GlobalExceptionHandler, excepciones de dominio
│   ├── security/              ← SecurityConfig, JwtProperties, TestSecurityConfig
│   └── web/                   ← ApiPaths (constantes /api/v1/...), PageResponse
├── config/                    ← @Configuration beans globales
├── auth/                      ← módulo de autenticación y perfiles
│   ├── api/                   ← REST controllers + DTOs
│   ├── domain/                ← entidades (User, Role) + value objects
│   ├── repository/            ← Spring Data repositories
│   └── service/               ← application services
└── caregivers/                ← módulo de gestión de cuidadores
    ├── api/
    ├── domain/
    ├── repository/
    └── service/
```

> **Convención**: todo controller expone bajo `/api/v1/...` usando `ApiPaths.V1`. Ver `common/web/ApiPaths.java`.

---

## Convenciones

- **Schema-first**: las migrations Flyway son la **fuente de verdad** del schema. Hibernate `ddl-auto=none/validate` solo valida que las entities coincidan con el schema (no lo modifica).
- **API REST**, JSON, sin estado, sin SSR.
- **Versionado** desde el día 1: `/api/v1/...`.
- **Errores** en formato RFC 7807 (`application/problem+json`) vía `GlobalExceptionHandler`.
- **TDD**: tests primero, integración con Testcontainers (no H2).
- **SOLID**: una responsabilidad por clase; preferir composición.
- **DTOs** separados de entidades: las entities no salen al exterior.
- **Lombok** para reducir boilerplate (`@Getter`, `@Builder`, `@RequiredArgsConstructor`).

---

## Flujo de trabajo Schema → Código → Docs

```
[1] Diseñar cambio en pizarrón / mental               ← conceptual
        ↓
[2] Crear nueva migration: db/migration/V2__xxx.sql
        ↓
[3] Levantar la app → Flyway aplica la migration
        ↓
[4] Regenerar el ER desde Flyway + Database → VP      ← docs académicos
        ↓
[5] Escribir/actualizar @Entity JPA (TDD)              ← ddl-auto=validate
        ↓
[6] Si valida OK → commit. Si no → ajustar migration o entity.
```

### Generar el ER desde la DB en Visual Paradigm Community

1. Tools → DB → **Reverse DDL** (NO JDBC — esa opción no está en Community)
2. Exportá el schema actual con `pg_dump --schema-only` (ver abajo)
3. Cargá el `.sql` en VP
4. Se genera el ER automáticamente
5. Ajustes visuales (colores, posiciones, notas)
6. Export PNG/PDF para entregables del sílabo

### Exportar el schema para VP

```bash
docker exec pgmq-ciudar-db pg_dump -U postgres --schema-only --no-owner DBCUIDAR > schema.sql
```

---

## Modelo de datos

21 tablas organizadas en 12 módulos:

| Módulo | Tablas |
|---|---|
| 0. Auth | `usuario`, `rol`, `usuario_rol` |
| 1. Familia | `familia` |
| 2. Cuidador | `cuidador`, `certificacion`, `experiencia_laboral`, `disponibilidad`, `tarifa` |
| 3. Verificación | `solicitud_verificacion`, `documento_verificacion` |
| 4. Adulto mayor | `adulto_mayor` |
| 5. Favoritos | `favorito` |
| 6. Reservas | `reserva`, `historial_estado_reserva` |
| 7. Pagos | `pago` |
| 8. Chat | `conversacion`, `mensaje` |
| 9. Seguimiento | `reporte_diario` |
| 10. Reseñas | `resena` |
| 11. Notificaciones | `notificacion` |

Detalle completo en `api/src/main/resources/db/migration/V1__init_schema.sql`.

---

## Pendiente / TODO

- [x] Integración inicial del Frontend (Auth, Rutas, Vite).
- [ ] Conectar a un IdP real para JWT en `prod` (Keycloak, Auth0, etc.). El `HmacJwtService` actual usa un secret HMAC hardcoded solo para dev.
- [ ] Mover el JWT signing key a variable de entorno (con `JwtProperties`).
- [ ] Implementar el envío de email real (SMTP) — hoy el token de verificación se auto-verifica en desarrollo a través del frontend.
- [ ] Wire el `InMemoryNotificacionRepository` al jOOQ `notificacion` table (sustituir impl).
- [ ] Productores/consumidores PGMQ (la cola ya está en docker-compose).

## Endpoints actuales

### Auth (públicos, no requieren JWT)

```bash
# RF01 - Registro (devuelve cabecera X-Dev-Verification-Token con el token)
curl -i -X POST http://localhost:8080/api/v1/auth/registro \
     -H 'Content-Type: application/json' \
     -d '{"email":"[email protected]","password":"Secreta123!","nombre":"Maria","apellido":"Lopez","telefono":"+51999999999"}'

# RF02 - Login
curl -i -X POST http://localhost:8080/api/v1/auth/login \
     -H 'Content-Type: application/json' \
     -d '{"email":"[email protected]","password":"Secreta123!"}'

# RF03 - Verificar email (con el token devuelto en X-Dev-Verification-Token)
curl -i -X POST 'http://localhost:8080/api/v1/auth/verificar-email?token=<PEGAR_TOKEN>'
```

### Auth (requieren JWT - Bearer header)

```bash
TOKEN=<pegar accessToken del login>

# RF04 - Ver mi perfil
curl -i -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/v1/auth/me

# RF04 - Actualizar mi perfil
curl -i -X PUT -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
     -d '{"nombre":"Ana","apellido":"Pérez","telefono":"+51888888888"}' \
     http://localhost:8080/api/v1/auth/me
```

### Notificaciones (RF05 - requieren JWT)

```bash
# Listar mis notificaciones (paginado)
curl -i -H "Authorization: Bearer $TOKEN" 'http://localhost:8080/api/v1/notificaciones?page=0&size=20'

# Marcar una como leída
curl -i -X POST -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/v1/notificaciones/2/leido
```

---

