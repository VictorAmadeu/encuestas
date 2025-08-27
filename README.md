# Plataforma de Encuestas 
Plataforma monorepo para crear y gestionar encuestas en línea. El backend está desarrollado con **Spring Boot 3** (Java 21) y persiste datos en **PostgreSQL** y **MongoDB**. El frontend se implementará con **Angular 17**.

## Requisitos
- Java 17+ (usamos 21)
- Node LTS (usamos 20 con NVM) + Angular CLI
- Docker Desktop (PostgreSQL + MongoDB)
- IntelliJ IDEA Community, VS Code

## Estructura
- **Java 21** y Maven 3.x (puedes usar el wrapper `mvnw` incluido)
- **Node.js 20** y Angular CLI (para el frontend)
- **Docker** y **docker-compose** para las bases de datos
- **Git Desktop**


## Arquitectura del repositorio

```
encuestas/
├── backend/        # API REST con Spring Boot
├── frontend/       # Aplicación Angular (pendiente)
└── docker-compose.yml
```

### Backend

- Framework: Spring Boot 3, Spring Security, JPA, Spring Data MongoDB
- Migraciones de BD con Flyway (`backend/src/main/resources/db/migration`)
- Documentación de API con springdoc (Swagger UI en `/swagger-ui.html`)
- Perfiles de configuración: `dev` (por defecto) y `prod`

### Frontend

El directorio `frontend/` alojará una SPA en Angular 17. Aún no forma parte del repositorio.

## Puesta en marcha (desarrollo)

1. Clona el repositorio y entra en él:

   ```bash
   git clone <url-del-repo>
   cd encuestas
   ```

2. Levanta PostgreSQL y MongoDB:

   ```bash
   docker-compose up -d
   ```

3. Ejecuta el backend con el perfil `dev`:

   ```bash
   ./mvnw -f backend/pom.xml spring-boot:run
   ```

4. Accede a la API en `http://localhost:8080` y a la documentación en `http://localhost:8080/swagger-ui.html`.

5. (Cuando esté disponible) ejecuta el frontend:

   ```bash
   cd frontend
   npm install
   ng serve
   ```

## Configuración

- El perfil activo por defecto es `dev` (`application.yml`). Para producción define la variable `SPRING_PROFILES_ACTIVE=prod`.
- Variables de entorno usadas en `application-prod.yml`:
  - `POSTGRES_URL`, `POSTGRES_USER`, `POSTGRES_PASSWORD`
  - `MONGODB_URI`
  - `SERVER_PORT`
- Ajusta `app.cors.allowed-origins` según el dominio del frontend.

## Migraciones de base de datos

Los scripts de Flyway se ubican en `backend/src/main/resources/db/migration`. Se ejecutan automáticamente al iniciar la aplicación.

## Pruebas

Ejecuta las pruebas unitarias del backend:

```bash
mvn -f backend/pom.xml test
```

## Contribuir

1. Haz un *fork* del repositorio.
2. Crea una rama para tu cambio (`git checkout -b feature/mi-mejora`).
3. Realiza *commit* y envía un *pull request* describiendo tus cambios.

## Licencia

Este proyecto no especifica licencia. Úsalo bajo tu propia responsabilidad.

