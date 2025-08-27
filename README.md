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
