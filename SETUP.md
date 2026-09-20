# SETUP — levantar CuidAR API en local

Guía mínima para clonar y tener la app corriendo. Asume Windows / macOS / Linux con Docker y Java 21.

> Si tenés algún paso distinto en tu máquina, decime y actualizo este doc.

---

## TL;DR — 3 comandos

```bash
git clone <URL_DEL_REPO>
cd <repo>
docker compose up -d postgres-pgmq && ./api/mvnw -f api/pom.xml spring-boot:run
```

Después: `curl http://localhost:8080/actuator/health` debería devolver `{"status":"UP"}`.

Si algo falla, abajo está paso a paso + troubleshooting.

---

## Requisitos

| Herramienta | Versión | Para qué |
|---|---|---|
| **Docker Desktop** | 4.x | Levantar PostgreSQL + pgAdmin |
| **Java JDK** | 21 | Correr la app (`./mvnw` falla con JDK < 21) |
| **Git** | 2.40+ | Clonar el repo |

El Maven **no** hace falta — el repo trae `mvnw` / `mvnw.cmd` (Maven Wrapper) que descarga su propia versión.

Verificar rápido:

```bash
docker --version
java -version     # debe decir "21.x"
```

---

## Paso a paso

### 1) Clonar el repo

```bash
git clone <URL>
cd <nombre-del-repo>
```

### 2) Levantar la base de datos

Desde la **raíz del repo**:

```bash
docker compose up -d postgres-pgmq
```

Esto arranca:
- Postgres 18 con extensión PGMQ en `localhost:5440`
- pgAdmin en `http://localhost:8083` (login `admin@admin.com` / `admin`)

**Importante**: `docker compose` (con espacio, no `docker-compose` con guión). Si tenés la versión vieja, reemplazalo.

Verificar que quedó healthy:

```bash
docker ps --filter "name=pgmq"
# STATUS debe decir "Up ... (healthy)"
```

### 3) (Opcional) Configurar variables de entorno

Por defecto **no necesitás hacer nada**. La app usa valores de desarrollo hardcoded para Postgres (`postgres` / `devpw_2025`).

Si querés cambiar algo (por ejemplo apuntar a otra DB), copiá el template y editá:

```bash
cp .env.example .env
# editá .env con tus valores
```

> ⚠️ Hoy `.env` es **opcional**. Cuando se active dotenv-java en el futuro, será leído automáticamente (ver sección "Variables de entorno" más abajo).

### 4) Arrancar la aplicación

Desde la **raíz del repo**:

```bash
./api/mvnw -f api/pom.xml spring-boot:run
```

O si estás en Windows / PowerShell:

```powershell
.\api\mvnw.cmd -f api/pom.xml spring-boot:run
```

La primera vez tarda ~1-2 minutos (descarga deps + Flyway aplica migrations + jOOQ genera código). Las siguientes arrancan en ~10-15 segundos.

Vas a ver en el log algo como:

```
Successfully applied 2 migrations to the database
Successfully validated 22 tables
Generating table : Usuario.java ...
Tomcat started on port 8080
Started ApiApplication in X.XXX seconds
```

### 5) Verificar

```bash
curl http://localhost:8080/actuator/health
# Esperado: {"status":"UP"}
```

Si ves `UP`, todo está andando.

---

## Endpoints disponibles (resumen)

| Método | Path | Auth | Para qué |
|---|---|---|---|
| `POST` | `/api/v1/auth/registro` | público | RF01 — crear cuenta |
| `POST` | `/api/v1/auth/login` | público | RF02 — devuelve JWT |
| `POST` | `/api/v1/auth/verificar-email?token=X` | público | RF03 — activa la cuenta |
| `GET` `/PUT` | `/api/v1/auth/me` | JWT | RF04 — mi perfil |
| `GET` | `/api/v1/notificaciones` | JWT | RF05 — mis notificaciones |
| `POST` | `/api/v1/notificaciones/{id}/leido` | JWT | marcar como leída |

En dev el `JwtDecoder` acepta cualquier token, así que para probar el `GET /me` basta con mandar `Authorization: Bearer lo-que-sea`.

---

## Variables de entorno (resumen)

Spring ya lee env vars del sistema. Si exportás estas antes de arrancar, toman precedencia sobre los defaults:

| Variable | Default (dev) | Para qué |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5440/DBCUIDAR` | Conexión a Postgres |
| `DB_USERNAME` | `postgres` | Usuario de la DB |
| `DB_PASSWORD` | `devpw_2025` | Password de la DB |
| `JWT_ISSUER_URI` | `http://localhost:9000` | URL del IdP (placeholder en dev) |
| `SERVER_PORT` | `8080` | Puerto HTTP |
| `SPRING_PROFILES_ACTIVE` | `dev` | Perfil Spring |

En **`dev`** los defaults andan sin que exportes nada.
En **`prod`** todos son obligatorios — si falta alguno la app falla al arrancar (es lo correcto).

---

## Tests

```bash
# desde api/
./mvnw test                 # Unix
.\mvnw.cmd test             # Windows
```

22 unit tests verdes (Mockito puro, sin DB). Tests de integración con Testcontainers están deshabilitados en este host por un issue de named pipe en Windows — ver troubleshooting.

---

## Troubleshooting común

### "docker compose: command not found"
Tenés la versión vieja. Probá `docker-compose up -d postgres-pgmq` (con guión). O instalá Docker Desktop 4.x.

### "Connection refused" en `localhost:5440`
La DB no está corriendo. Verificá:
```bash
docker ps --filter "name=pgmq"
```
Si no aparece, `docker compose up -d postgres-pgmq`.

### "Address already in use: bind" en puerto 8080
Otra app usa el 8080. Opciones:
```bash
SERVER_PORT=8085 ./api/mvnw -f api/pom.xml spring-boot:run
```
O matá el proceso que esté ocupando el puerto (`Get-NetTCPConnection -LocalPort 8080` en PowerShell).

### Las clases jOOQ no están en `src/main/java/com/cuidar/api/jooq/generated/`
Maven decidió no regenerar (cache). Forzá:
```bash
./mvnw -f api/pom.xml clean jooq-codegen:generate
```

### Tests con `BadRequestException (Status 400)` mencionando Testcontainers
Issue conocido de Testcontainers + named pipe en Windows. **Solo afecta integration tests**, no toca el flujo normal de la app. Para los unit tests con mocks no hace falta arreglarlo.

### Cambié una migration (V_n) y la app no la aplica
Flyway solo aplica migrations nuevas en arranque. Si tocás una existente, hay que dropear el schema y empezar de cero (solo dev):
```bash
docker exec -i pgmq-ciudar-db psql -U postgres -d DBCUIDAR -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;"
# al re-arrancar la app, Flyway las reaplica
```

> ⚠️ Esto borra todos los datos de dev. Úsalo solo si sabés lo que hacés.

---

## Estructura del repo

```
proyecto-CISS/
├── api/                   ← backend Spring Boot (acá se trabaja la mayoría del tiempo)
├── frontend/              ← vacío por ahora
├── docker-compose.yml     ← Postgres + pgAdmin
├── .env.example           ← template de variables (copialo a .env si necesitás)
├── .gitignore / .gitattributes
└── README.md / SETUP.md
```

Para detalles técnicos de la API consultá `api/README.md` (si existe) o los comentarios en el código fuente.

---

## Reglas del equipo

- Mensajes de commit con **conventional commits** (`feat:`, `fix:`, `chore:`, `docs:`, `refactor:`). Sin scope amplio (`feat(auth): ...`).
- **No agregar `Co-Authored-By` ni atribución de IA en los commits.**
- Antes de pushear: corré `./mvnw test` y verificá que los 22 tests pasen.
