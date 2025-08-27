# Plataforma de Encuestas 📊

Este repositorio alberga el backend y la configuración inicial de una plataforma de encuestas desarrollada con **Spring Boot**, pensada para combinarse con un cliente **Angular** (por implementar). Su objetivo es permitir la creación, edición y publicación de encuestas en línea, la gestión de usuarios y el almacenamiento de resultados en distintos motores de base de datos.

---

## Índice

- [Descripción general](#descripción-general)
- [Características](#características)
- [Arquitectura y estructura del proyecto](#arquitectura-y-estructura-del-proyecto)
- [Requisitos previos](#requisitos-previos)
- [Instalación y puesta en marcha](#instalación-y-puesta-en-marcha)
- [Configuración de bases de datos](#configuración-de-bases-de-datos)
- [Migraciones con Flyway](#migraciones-con-flyway)
- [Perfiles y configuración](#perfiles-y-configuración)
- [Seguridad y JWT](#seguridad-y-jwt)
- [Documentación de la API](#documentación-de-la-api)
- [Contribuciones](#contribuciones)
- [Licencia](#licencia)
- [Referencias](#referencias)

---

## Descripción general

La **Plataforma de Encuestas** es un proyecto educativo/experimental cuyo objetivo es proporcionar una base sólida para construir un sistema de encuestas completo. El backend está construido en **Java 21** empleando **Spring Boot 3** y varias dependencias clave:

- **Web MVC** para exponer endpoints REST;
- **Spring Data JPA** con PostgreSQL para almacenar la información estructural (usuarios, encuestas, preguntas y opciones);
- **Spring Data MongoDB** para persistir las respuestas de los participantes de forma flexible;
- **Spring Security** con autenticación mediante JWT;
- **Flyway** para versionar el esquema de la base de datos SQL;
- **Springdoc OpenAPI** para documentar la API de forma automática.

Actualmente el proyecto se centra en el backend y la configuración de las bases de datos. Se espera añadir una aplicación frontend Angular en el futuro para ofrecer una interfaz de usuario completa.

---

## Características

- 🧑‍💼 **Gestión de usuarios**: las cuentas se almacenan en la tabla `users` de PostgreSQL e incluyen un rol (p. ej. `ADMIN`, `USER`) y estado de habilitación.
- 📝 **Creación y publicación de encuestas**: cada encuesta (`surveys`) pertenece a un usuario, tiene un título, descripción y estado (borrador, publicada, cerrada, etc.).
- ❓ **Soporte para distintos tipos de preguntas**: la tabla `questions` permite definir preguntas de texto o selección con un orden configurable.
- ✅ **Opciones de respuesta**: las opciones (`options`) vinculan preguntas y posibles respuestas en caso de preguntas cerradas.
- 🗄️ **Persistencia híbrida**: estructura relacional en PostgreSQL y almacenamiento de respuestas en MongoDB.
- 🔀 **Versionado de bases de datos**: mediante Flyway se aplican scripts SQL al arrancar la aplicación, garantizando que el esquema está actualizado.
- 🔐 **Seguridad**: integrada con Spring Security y tokens JWT para proteger los endpoints.
- 📄 **Documentación automática**: gracias a springdoc-openapi, los endpoints REST cuentan con documentación interactiva (Swagger UI).

---

## Arquitectura y estructura del proyecto

La raíz del repositorio presenta varios archivos y carpetas importantes:

| Directorio/archivo | Descripción |
|---|---|
| `docker-compose.yml` | Orquesta dos servicios de base de datos: PostgreSQL y MongoDB. Expone los puertos **5432** y **27017** respectivamente, declara volúmenes persistentes (`dbdata`, `mongodata`) y *healthchecks* para cada contenedor **[1]**. |
| `backend/` | Contiene el código fuente y configuración del backend Spring Boot. |
| `backend/pom.xml` | Archivo Maven con las dependencias necesarias (Spring Boot, JPA, MongoDB, Security, Lombok, Flyway, MapStruct, JWT, Springdoc, etc.) **[2]**. |
| `backend/src/main/java/com/acme/encuestas` | Paquete raíz del código Java (por ejemplo, la clase `EncuestasApplication` que inicia Spring Boot) **[3]**. |
| `backend/src/main/resources/application.yml` | Configuración base de Spring donde se establece el nombre de la aplicación y el **perfil por defecto `dev`** **[4]**. |
| `backend/src/main/resources/application-dev.yml` | Configuración específica para desarrollo: datos de acceso a Postgres/Mongo, ajustes de JPA, Flyway, Springdoc, JWT y niveles de *logging* **[5]**. |
| `backend/src/main/resources/db/migration/` | Carpeta con scripts de migración Flyway. La primera migración (`V1__init.sql`) crea las tablas `users`, `surveys`, `questions` y `options` con sus claves foráneas e índices **[6]**. |

> **Nota:** todavía no existe un módulo `/frontend/`; se añadirá más adelante cuando se integre la aplicación Angular.

---

## Requisitos previos

Antes de arrancar el proyecto asegúrate de contar con los siguientes componentes en tu máquina de desarrollo:

| Herramienta | Versión recomendada | Uso |
|---|---|---|
| **Java JDK** | 21 (también se puede usar 17) | Necesario para compilar y ejecutar el backend Spring. |
| **Maven** | Usa el *wrapper* `./mvnw` incluido | Gestiona dependencias y permite compilar/arrancar la aplicación. |
| **Docker & Docker Compose** | Última versión estable | Se usa para levantar Postgres y Mongo de forma aislada. |
| **Node.js & Angular CLI** | (opcional) | Útiles para el cliente Angular una vez se desarrolle. |

---

## Instalación y puesta en marcha

1) **Clona el repositorio:**

```bash
git clone https://github.com/VictorAmadeu/encuestas.git
cd encuestas
```

2) **Levanta las bases de datos con Docker Compose:**

```bash
# arranca Postgres y Mongo en segundo plano
docker compose up -d

# comprueba el estado
docker compose ps
```

Al iniciar por primera vez, Docker descargará las imágenes `postgres:16` y `mongo:7`. El archivo `docker-compose.yml` expone los puertos predeterminados y declara volúmenes persistentes para que los datos no se pierdan al reiniciar **[1]**.

3) **Ejecuta el backend Spring Boot:**

```bash
cd backend
# compila (opcional: salta tests)
./mvnw -DskipTests package
# arranca la aplicación con perfil dev
./mvnw spring-boot:run
```

Durante el arranque verás en el log cómo Flyway crea la tabla de historial (`flyway_schema_history`) y aplica la migración `V1__init.sql` **[6]**. Una vez la aplicación esté lista, podrás acceder a los endpoints en <http://localhost:8080>.

4) **Comprueba que las tablas están creadas (opcional):**

Ejecuta los siguientes comandos para conectarte al contenedor Postgres y listar las tablas:

```bash
docker compose exec postgres psql -U encuestas -d encuestas -c "\dt"
docker compose exec postgres psql -U encuestas -d encuestas -c "\d+ users"
```

Deberías ver las tablas `users`, `surveys`, `questions` y `options` junto con la tabla de Flyway.

---

## Configuración de bases de datos

El proyecto utiliza dos bases de datos distintas para optimizar el almacenamiento de distinta naturaleza:

### PostgreSQL 16

Se usa para la información estructurada (usuarios, encuestas, preguntas, opciones). La configuración de acceso está en `application-dev.yml` **[7]**. Al levantarse con Docker Compose, se establece:

- **Nombre de la base:** `encuestas`
- **Usuario:** `encuestas`
- **Contraseña:** `encuestas`
- **Puerto expuesto:** `5432`

### MongoDB 7

Destinada a almacenar las respuestas de las encuestas. Se configura en `application-dev.yml` mediante la URI:

```
mongodb://encuestas:encuestas@localhost:27017/encuestas?authSource=admin
```

Los datos se guardan en el contenedor `encuestas-mongo` usando el volumen `mongodata` **[8]**.

---

## Migraciones con Flyway

Para mantener el esquema de PostgreSQL versionado y reproducible se emplea **Flyway**. Los scripts de migración se ubican en `backend/src/main/resources/db/migration/`. Cada archivo debe seguir el formato `V<versión>__<nombre>.sql`.

- La primera migración (`V1__init.sql`) crea las tablas y los índices principales **[6]**.
- **Opcional:** existe una segunda migración (`V2__drop_redundant_unique_index.sql`) para eliminar un índice redundante (`ux_users_email`) en la tabla `users`, que puede añadirse más adelante.

---

## Perfiles y configuración

Spring Boot permite definir distintos perfiles (por ejemplo, `dev`, `prod`, `test`) y cargar archivos de configuración específicos. En este proyecto:

- `application.yml` contiene la configuración común a todos los perfiles, incluida la propiedad `spring.application.name` y el perfil por defecto (`spring.profiles.default=dev`) **[9]**.
- `application-dev.yml` define las propiedades para el perfil `dev`: URL JDBC, credenciales, ajustes de JPA y Flyway, URI de MongoDB y niveles de log **[5]**.

En producción se debería crear un `application-prod.yml` con las credenciales reales, puertos distintos y variables seguras (por ejemplo, el secreto JWT se debe leer de variables de entorno). Los perfiles se pueden activar pasando el parámetro `--spring.profiles.active=prod` al arranque.

---

## Seguridad y JWT

El backend incluye **Spring Security** para proteger los endpoints. Se utiliza autenticación basada en **JSON Web Tokens (JWT)**, con ayuda de la biblioteca **JJWT**. El secreto y la duración del token se encuentran en `application-dev.yml` **[10]**; no deben reutilizarse en producción. Próximamente se incorporarán controladores para *login*, *registro* y protección por roles.

---

## Documentación de la API

Para facilitar el uso y prueba de los endpoints REST, el proyecto integra **springdoc-openapi**. Al arrancar la aplicación, la especificación OpenAPI queda disponible en:

- **JSON:** <http://localhost:8080/v3/api-docs>
- **Swagger UI:** <http://localhost:8080/swagger-ui.html>

Estas interfaces generan automáticamente la descripción de los recursos, métodos HTTP, parámetros y respuestas.

---

## Contribuciones

Se trata de un proyecto abierto y en evolución. Las contribuciones son bienvenidas, ya sea reportando problemas, sugiriendo nuevas funcionalidades o enviando *pull requests*. Para colaborar:

1. Haz un *fork* del repositorio y crea una rama a partir de `main`.
2. Implementa tus mejoras y asegúrate de que el proyecto compila y los contenedores se levantan correctamente.
3. Envía un *pull request* detallando los cambios realizados y su motivación.

---

## Licencia

El proyecto se ofrece **sin licencia explícita** en esta fase inicial. Esto significa que su uso está permitido con fines educativos o personales, pero no proporciona una garantía legal ni comercial. En futuras versiones se añadirá una licencia abierta (por ejemplo **MIT** o **Apache 2.0**) para favorecer su distribución y reutilización.

---

Este README se generó de forma automatizada a partir de la configuración y los *scripts* presentes en la rama `main` del repositorio en **agosto de 2025**. Si detectas algún error o desactualización, no dudes en abrir una incidencia.

---

## Referencias

[1] `docker-compose.yml`  
<https://github.com/VictorAmadeu/encuestas/blob/main/docker-compose.yml>

[2] `pom.xml`  
<https://github.com/VictorAmadeu/encuestas/blob/main/backend/pom.xml>

[3] `EncuestasApplication.java`  
<https://github.com/VictorAmadeu/encuestas/blob/main/backend/src/main/java/com/acme/encuestas/EncuestasApplication.java>

[4] `application.yml`  
<https://github.com/VictorAmadeu/encuestas/blob/main/backend/src/main/resources/application.yml>

[5] `application-dev.yml`  
<https://github.com/VictorAmadeu/encuestas/blob/main/backend/src/main/resources/application-dev.yml>

[6] `V1__init.sql`  
<https://github.com/VictorAmadeu/encuestas/blob/main/backend/src/main/resources/db/migration/V1__init.sql>

[7] `application-dev.yml` (igual que [5])  
<https://github.com/VictorAmadeu/encuestas/blob/main/backend/src/main/resources/application-dev.yml>

[8] `application-dev.yml` (igual que [5])  
<https://github.com/VictorAmadeu/encuestas/blob/main/backend/src/main/resources/application-dev.yml>

[9] `application.yml` (igual que [4])  
<https://github.com/VictorAmadeu/encuestas/blob/main/backend/src/main/resources/application.yml>

[10] `application-dev.yml` (igual que [5])  
<https://github.com/VictorAmadeu/encuestas/blob/main/backend/src/main/resources/application-dev.yml>

