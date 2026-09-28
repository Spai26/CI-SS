# Documentación Integral - CI-SS

El **Centro Integral - Servicios de Salud (CI-SS)** es una plataforma digital que facilita la conexión entre familias que necesitan servicios de cuidado para adultos mayores y cuidadores capacitados y verificados. 

## 1. Arquitectura del Sistema

La solución está construida sobre una arquitectura de microservicios o monolito modular, separando claramente el frontend del backend y haciendo uso intensivo de contenedores para la infraestructura.

### 1.1. Backend (API REST)
El backend está construido con **Java 21** y **Spring Boot 3**.
- **Acceso a Datos**: Utiliza **jOOQ** para un acceso a la base de datos seguro en tipos y fluido (sin ORM tradicionales como Hibernate).
- **Migraciones**: El esquema y migraciones de la base de datos están gestionados por **Flyway**.
- **Seguridad**: Autenticación y Autorización mediante **Spring Security** y OAuth2 (Resource Server), asegurando endpoints mediante tokens.
- **Módulos Principales**:
  - `familias`: Gestión de registros de familiares y el perfil clínico y general de los adultos mayores (entidad `AdultoMayor`).
  - `cuidadores` / `verificaciones`: Flujos para revisar solicitudes, verificar credenciales y aprobar perfiles en la plataforma (ej. `VerificacionAdminController`).
  - `reservas`: Manejo del ciclo de vida de los servicios solicitados a los cuidadores (`ReservaJooqRepository`, `Reserva`).
  - `resenas`: Sistema de calificación para garantizar la retroalimentación de calidad sobre los cuidadores (`ResenaService`).
  - `reportes`: Funcionalidades administrativas para monitorear la salud del negocio y los servicios (`ReporteDiarioController`).

### 1.2. Frontend
El cliente es una aplicación SPA (Single Page Application) construida con **React** y **TypeScript**.
- **Componentes**: Utiliza modales interactivos (`BookingModal.tsx`) e interfaces ricas.
- Se comunica de manera estricta y tipada con la API backend.

### 1.3. Infraestructura y Base de Datos
- **PostgreSQL**: Se utiliza una imagen enriquecida (`pg18-pgmq`) orquestada mediante Docker Compose, permitiendo también colas de mensajes si fuesen necesarias en el futuro.
- **Docker Compose**: Todo el entorno de desarrollo y dependencias (BD, etc.) se levanta desde el archivo `docker-compose.yml`.

## 2. API y JavaDoc

La documentación completa a nivel de código para el backend ha sido generada exitosamente utilizando JavaDoc y Maven. El proceso omitió la regeneración del esquema local jOOQ para evitar dependencias directas del contenedor en tiempo de construcción del doc.

- Puedes acceder al **índice principal del JavaDoc** generado abriendo el siguiente archivo en tu navegador web:
  - `api/target/reports/apidocs/index.html`

## 3. Próximos Pasos y Mantenimiento

Para regenerar la documentación Javadoc en el futuro (siempre y cuando hagas cambios al código):
```bash
# Dentro del directorio /api, ejecuta:
mvnw.cmd javadoc:javadoc "-Djooq.codegen.skip=true"
```
*(Se usa `-Djooq.codegen.skip=true` para no depender de la conexión a la base de datos durante la sola generación de la documentación).*
