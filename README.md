# Plataforma de encuestas – Backend Spring Boot

Bienvenido al repositorio **encuestas**, una base para construir una plataforma web de encuestas. Este proyecto ofrece un backend escrito en **Java 21** utilizando **Spring Boot 3** y persiste los datos en **PostgreSQL** y **MongoDB**. En el futuro se añadirá un cliente **Angular** para completar la experiencia de usuario, pero actualmente el enfoque principal es el servicio REST y la configuración de bases de datos.

## Índice

1. Características principales
2. Arquitectura y estructura del proyecto
3. Requisitos previos
4. Instalación paso a paso
5. Configuración
6. Uso de la API
7. Estructura de carpetas
8. Pruebas
9. Despliegue
10. Solución de problemas
11. Seguridad y buenas prácticas
12. Rendimiento y optimización
13. Internacionalización y accesibilidad
14. Roadmap y estado del proyecto
15. Cómo contribuir
16. Código de conducta
17. Licencia y créditos

---

## Características principales

El backend proporciona una base sólida para desarrollar un sistema de encuestas completo. Entre sus principales características se incluyen:

• **Gestión de usuarios:** el modelo User almacena email, contraseña cifrada (bcrypt), rol (p. ej. USER o ADMIN) y marcas de creación/modificación[1]. El endpoint de autenticación (/api/auth/login) permite iniciar sesión y devuelve un token JWT válido.

• **Registro y autenticación con JWT:** los usuarios pueden registrarse mediante /api/auth/register, lo que crea un nuevo usuario y responde con un token JWT[2]. El token se firma utilizando la clave secreta definida en la configuración y contiene el identificador y el rol del usuario[3].

• **Persistencia híbrida**:** la información estructural (usuarios, encuestas, preguntas y opciones) se almacena en PostgreSQL. El script de migración V1\_\_init.sql crea las tablas users, surveys, questions y options con sus relaciones e índices[4]. Las respuestas de las encuestas se guardan en la colección responses de MongoDB, representadas por los documentos ResponseDocument, AnswerDocument y MetaData[5].

• **Seguridad basada en roles:** un filtro JWT (JwtFilter) intercepta cada petición, valida el token y establece la autenticación en el contexto de Spring[6]. La clase SecurityConfig declara reglas de acceso: rutas como /api/auth/**, /v3/api-docs/** o /swagger-ui/\*\* son públicas; los demás endpoints requieren autenticación y algunos exigen el rol ADMIN[7].

• **Versionado de bases de datos con Flyway:** al arrancar la aplicación se ejecutan las migraciones SQL localizadas en backend/src/main/resources/db/migration/[4]. Flyway se configura en application-dev.yml para crear un esquema base y aplicar futuras actualizaciones[8].

• **Documentación de la API:\*\* Springdoc-OpenAPI genera automáticamente la especificación OpenAPI y una interfaz Swagger UI accesible en /swagger-ui.html[9]. Esto facilita la prueba de los endpoints desde el navegador.


**Nota:** en la fecha de elaboración de este documento no existe un módulo de frontend; sólo se ha implementado el módulo backend. Los futuros endpoints para gestionar encuestas, preguntas y respuestas aún se encuentran **por completar**.

---

## Arquitectura y estructura del proyecto

### Breve descripción ####

La aplicación implementa uma arquitetura clássica de **Spring Boot** organizada em camadas: controlador (REST), serviço (lógica de negocio), repositório (acesso a dados) e modelos/entidades. Para otimizar persistência e consultas, o projeto usa duas bases de dados especializadas:

```
graph TD
  subgraph Cliente (futuro)
    A[Aplicación Angular]
  end
  A -- REST --> B[Backend Spring Boot]
  B -- JPA --> C[PostgreSQL]
  B -- Spring Data MongoDB --> D[MongoDB]

  C -- Estructura (users, surveys, questions, options) --> B
  D -- Respuestas (collections: responses) --> B
```

#### Componentes y responsabilidades (rápido) ###

- Backend (`backend/`): módulo Maven con el código Java. La clase principal `EncuestasApplication` arranca Spring Boot y carga los beans. Las entidades JPA principales son `User`, `Survey`, `Question` y `Option` (mappeadas a PostgreSQL).
- Persistência relacional (PostgreSQL): almacena la estructura del dominio — usuarios, encuestas, preguntas y opciones. Las entidades JPA y las migraciones Flyway viven en `backend/src/main/resources/db/migration/`.
- Persistência documental (MongoDB): guarda las respuestas de encuestas en la colección `responses` mediante documentos (`ResponseDocument`, `AnswerDocument`, `MetaData`) para consultas agregadas y almacenamiento flexible.
- Seguridad: `SecurityConfig` + `JwtFilter` implementan seguridad stateless con JWT. Para desarrollo hay un perfil alternativo (`DevSecurityConfig`) que desactiva restricciones cuando se necesita probar sin auth.
- Infra & desarrollo: `docker-compose.yml` orquestra PostgreSQL e MongoDB com volumes persistentes. As credenciais e URLs usadas em dev estão em `backend/src/main/resources/application-dev.yml`.

### Arquivos chave e onde buscar ###

- Código principal: `backend/src/main/java/com/acme/encuestas/EncuestasApplication.java`
- Entidades JPA: `backend/src/main/java/com/acme/encuestas/model/`
- Documentos MongoDB: `backend/src/main/java/com/acme/encuestas/document/`
- Repositórios: `backend/src/main/java/com/acme/encuestas/repository/`
- Configurações: `backend/src/main/resources/application.yml` (base) e `application-dev.yml` (dev)
- Migrações Flyway: `backend/src/main/resources/db/migration/V1__init.sql`

### Flujo de datos (essencial) ###

1. El cliente (futuro Angular) consume la API REST del backend.
2. El backend usa JPA para operaciones CRUD sobre PostgreSQL (estructura y metadatos de encuestas).
3. Cuando se guardan respuestas, se persisten como documentos en MongoDB para permitir esquemas flexibles y consultas agregadas.
4. Flyway gestiona la evolución del esquema en PostgreSQL al arrancar la aplicación.

### Por qué este enfoque ###

Usar PostgreSQL para el modelo relacional mantiene integridad referencial (encuestas, preguntas y opciones), mientras que MongoDB facilita almacenar respuestas con estructura variable y realizar agregaciones/consultas analíticas sin afectar el esquema relacional.

---

## Requisitos previos

Para clonar y ejecutar este proyecto necesitas:

| Herramienta                 | Versión recomendada                | Uso                                                              |
| --------------------------- | ---------------------------------- | ---------------------------------------------------------------- |
| **Java JDK**                | 21 (también se puede usar 17)      | Necesario para compilar y ejecutar el backend Spring.            |
| **Maven**                   | Usa el _wrapper_ `./mvnw` incluido | Gestiona dependencias y permite compilar/arrancar la aplicación. |
| **Docker & Docker Compose** | Última versión estable             | Se usa para levantar Postgres y Mongo de forma aislada.          |
| **Node.js & Angular CLI**   | (opcional)                         | Útiles para el cliente Angular una vez se desarrolle.            |

---

## Instalación paso a paso

1. **Clonar el repositorio:**

```bash
git clone https://github.com/VictorAmadeu/encuestas.git
cd encuestas
```

1. A**rrancar las bases de datos con Docker Compose:**

```bash
# levanta Postgres y Mongo en segundo plano
docker compose up -d

# verifica que están funcionando
docker compose ps
```

La primera vez que ejecutes este comando, Docker descargará las imágenes postgres:16 y mongo:7. Los contenedores usarán los puertos locales 5432 y 27017 y montarán volúmenes (dbdata y mongodata) para persistir los datos[17].

1. **Compilar y ejecutar el backend:**

```bash
cd backend
# compilar (omitimos tests por ahora)
./mvnw -DskipTests package
# arrancar la aplicación con el perfil dev
./mvnw spring-boot:run
```

Durante el arranque verás en los logs cómo Flyway aplica la migración V1\_\_init.sql y crea las tablas en Postgres[4]. Una vez iniciada, la API estará accesible en [http://localhost:8080](http://localhost:8080).

1. **Verificar el esquema de la base de datos (opcional):**

```bash
# accede al contenedor de Postgres y lista las tablas
docker compose exec postgres psql -U encuestas -d encuestas -c "\dt"
```

Deberías ver las tablas users, surveys, questions, options y flyway_schema_history.

---

## Configuración

### Perfiles de Spring Boot

Spring Boot permite cargar distintas configuraciones en función del perfil activo. El perfil por defecto es dev[13], cuya configuración se encuentra en application-dev.yml. Puedes crear otros archivos como application-prod.yml para producción y activarlos con:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

### Variables de entorno y credenciales

En desarrollo las credenciales están codificadas (usuario encuestas, contraseña encuestas tanto para Postgres como MongoDB). Nunca utilices estos valores en producción. En entorno productivo debes:

1. Cambiar el secreto JWT (jwt.secret) y el tiempo de expiración en un archivo application-prod.yml o mediante variables de entorno[18].
2. Configurar las URLs de PostgreSQL y MongoDB con usuarios y contraseñas seguros[19].
3. Restringir el origen CORS a tu dominio de frontend. Puedes modificar allowed-origins en application.yml o exportar APP_CORS_ALLOWED_ORIGINS para limitar los orígenes permitidos[20].

### Personalización del puerto y contexto

El puerto del servidor se define en application.yml o application-dev.yml (por defecto 8080). Puedes ajustarlo añadiendo `server.port=9000` al archivo de configuración o pasando `--server.port=9000` al ejecutar.

---

## Uso de la API

Actualmente sólo están disponibles los endpoints de autenticación. Los futuros controladores para encuestas, preguntas y respuestas se implementarán próximamente. A continuación se describen los endpoints existentes:

### Registro de usuario

POST /api/auth/register – crea un nuevo usuario y devuelve un token de acceso.

**Cuerpo de la solicitud (JSON):**

```json
{
  "email": "usuario@example.com",
  "password": "contraseñaSegura",
  "role": "USER"
}
```

**Respuesta exitosa (201 Created):**

```json
{
  "token": "<jwt>"
}
```

Si el correo ya existe, la API devuelve 409 Conflict[21].

### Inicio de sesión

POST /api/auth/login – valida las credenciales y retorna un token JWT.

**Cuerpo de la solicitud (JSON):**

```json
{
  "email": "usuario@example.com",
  "password": "contraseñaSegura"
}
```

**Respuesta exitosa (200 OK):**

```json
{
  "token": "<jwt>"
}
```

Si las credenciales son incorrectas o la cuenta está deshabilitada, se devuelve 401 Unauthorized[22].

### Endpoints pendientes

• **Gestión de encuestas:** creación, listado y eliminación de encuestas (/api/surveys).
• **Gestión de preguntas y opciones:** asociar preguntas a encuestas y opciones a preguntas.
• **Envío de respuestas:** endpoint para que un usuario responda una encuesta, almacenando sus respuestas en MongoDB.

Estas funcionalidades están **por completar;** se invita a la comunidad a colaborar en su implementación.

---

## Estructura de carpetas

Breve guía visual con los archivos y carpetas más relevantes del repositorio.

| Ruta / archivo                                   | Descripción                                                                                                           |
| ------------------------------------------------ | --------------------------------------------------------------------------------------------------------------------- |
| `docker-compose.yml`                             | Compose file que orquesta los contenedores de PostgreSQL y MongoDB [17].                                              |
| `backend/pom.xml`                                | Archivo Maven que define las dependencias (Spring Boot, JPA, MongoDB, Security, Flyway, MapStruct, Lombok, JWT) [23]. |
| `backend/mvnw`, `backend/mvnw.cmd`               | Wrappers de Maven para no depender de una instalación local.                                                          |
| `backend/src/main/java/com/acme/encuestas`       | Código fuente de la aplicación: clase principal, modelos, controladores, servicios, repositorios y configuración.     |
| `backend/src/main/resources/application.yml`     | Configuración base (nombre de la aplicación, perfil por defecto y CORS) [24].                                         |
| `backend/src/main/resources/application-dev.yml` | Configuración de desarrollo: credenciales de BD, Flyway, Swagger, JWT y niveles de log [25].                          |
| `backend/src/main/resources/db/migration/`       | Migraciones Flyway (scripts SQL versionados) [4].                                                                     |
| `backend/src/test/java/...`                      | Tests unitarios; actualmente sólo comprueba la carga del contexto Spring [26].                                        |

Árbol de ejemplo (raíz del repo):

```text
.
├─ docker-compose.yml
├─ backend/
│  ├─ mvnw
│  ├─ mvnw.cmd
│  ├─ pom.xml
│  └─ src/
│     ├─ main/
│     │  ├─ java/com/acme/encuestas/...
│     │  └─ resources/
│     │     ├─ application.yml
│     │     ├─ application-dev.yml
│     │     └─ db/migration/V1__init.sql
│     └─ test/
│        └─ java/...
└─ frontend/  (aún no existe — cuando se añada, debe ubicarse aquí)
```

Pequeña nota: se conservan las referencias numéricas ([n]) que apuntan a recursos y archivos del README.

---

## Pruebas

El proyecto incluye dependencias de **JUnit 5** y **Spring Boot Test**, pero sólo cuenta con una prueba básica que comprueba la carga del contexto[26]. Para ejecutar los tests:

```bash
cd backend
./mvnw test
```

Se anima a añadir pruebas unitarias y de integración para los servicios y controladores cuando se implementen. Herramientas como **Mockito** y **Spring Security Test** ya están declaradas en el pom.xml[27].

---

## Despliegue

### Entorno local

En desarrollo, la manera recomendada es usar Docker Compose para bases de datos y ejecutar el backend desde el IDE o con ./mvnw spring-boot\:run. Puedes personalizar variables y puertos en application-dev.yml.

### Producción

Para un despliegue de producción debes:

1. **Crear un perfil de producción (** application-prod.yml **)** con credenciales seguras, secreto JWT robusto y CORS restringido.
2. **Empaquetar el backend como JAR** con ./mvnw -Pprod clean package (puedes definir un perfil Maven para producción). El JAR resultante (en target/) se ejecuta con java -jar encuestas-0.0.1-SNAPSHOT.jar.
3. **Provisionar bases de datos** en un proveedor cloud (p. ej., Amazon RDS y Atlas) o mediante servicios gestionados en la nube. Ajusta las URL de conexión en la configuración.
4. **Servir el frontend** (cuando exista) como aplicación estática o desplegarlo en un CDN.

Opcionalmente puedes crear una imagen Docker del backend e integrarla en el docker-compose.yml para desplegar todo el stack junto.

---

## Solución de problemas

| Problema                                                                 | Posible causa y solución                                                                                                                                                                                                        |
| ------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **La aplicación no arranca y muestra** Port 5432 is already in use       | Otro proceso (otra base o contenedor) está usando el puerto 5432. Cambia el mapeo de puertos en docker-compose.yml (por ejemplo 5433:5432) y actualiza application-dev.yml en la URL JDBC.                                      |
| org.postgresql.util.PSQLException: FATAL: password authentication failed | Asegúrate de que los valores de POSTGRES_USER y POSTGRES_PASSWORD en docker-compose.yml coinciden con username y password en application-dev.yml[28]. Elimina los volúmenes (docker compose down -v) si modificas credenciales. |
| **No se ejecutan las migraciones**                                       | Confirma que Flyway está habilitado (spring.flyway.enabled=true en application-dev.yml[8]). Verifica que los scripts se encuentran en classpath:db/migration/ y tienen nombres V<version>\_\_<descripcion>.sql.                 |
| **Recibo** 401 Unauthorized **en endpoints privados**                    | Debes incluir un encabezado Authorization: Bearer <jwt> válido en la petición. Obtén el token mediante /api/auth/login o /api/auth/register.                                                                                    |
| Invalid JWT signature **o expiración**                                   | El token ha sido manipulado o caducado. Genera uno nuevo y, en producción, cambia el secreto JWT y limita el tiempo de validez[29].                                                                                             |

Para otros problemas, consulta los logs de la aplicación y de los contenedores (con docker compose logs).

---

## Seguridad y buenas prácticas

Este proyecto ofrece un buen punto de partida, pero debe ajustarse a las mejores prácticas de seguridad antes de usarse en entornos reales:

1. **Secrets:** nunca subas secretos o contraseñas al repositorio. Mueve el secreto JWT y las credenciales de bases de datos a variables de entorno o a un gestor de secretos en producción[18].
2. **CORS:** define explícitamente los orígenes permitidos para la API en application.yml[20]. No uses \* en producción.
3. **Roles y permisos:** revisa las reglas en SecurityConfig y define roles granulares. Usa anotaciones como @PreAuthorize en servicios para proteger operaciones sensibles.
4. **Validez de los tokens:** la clase JwtUtil firma los tokens con HS256 y una clave Base64; considera usar claves rotativas y refresco de tokens para mayor seguridad[30].
5. **Protección de datos:** implementa control de acceso para evitar que un usuario obtenga encuestas que no son suyas, o responda varias veces a la misma encuesta (ya existe un método existsBySurveyIdAndRespondentId en ResponseRepository[31] para prevenir envíos duplicados).

---

## Rendimiento y optimización

Aunque el proyecto es pequeño, se pueden adoptar medidas para mejorar el rendimiento:

• **Lazily loaded collections:** las relaciones JPA (Survey.questions, Question.options) están definidas con fetch = LAZY para evitar cargar datos innecesarios[32]. Añade consultas específicas en los repositorios para evitar el n+1 problem.
• **Indices adecuados:** la migración inicial crea índices para búsquedas por email, propietario de la encuesta, preguntas por encuesta y opciones por pregunta[33].
• **Paginación y caché:** cuando se implementen los listados de encuestas, considera usar Pageable en Spring Data y cachés (Caffeine/Redis) para consultas frecuentes.

---

## Internacionalización y accesibilidad

Actualmente no se incluyen mecanismos de internacionalización (i18n) ni accesibilidad. La futura aplicación Angular debería emplear las capacidades de i18n de Angular y seguir las normas WCAG. En el backend se pueden usar mensajes externalizados con messages.properties y la anotación @MessageSource. Esta sección está **por completar**.

---

## Roadmap y estado del proyecto

El repositorio está en fase **experimental/educativa**. Las siguientes tareas clave se encuentran en el plan de trabajo:

• [ ] Implementar controladores REST para encuestas, preguntas y opciones.
• [ ] Crear servicios y casos de uso para la creación, actualización y eliminación de encuestas.
• [ ] Añadir un endpoint para enviar y almacenar respuestas en MongoDB y evitar duplicados utilizando ResponseRepository.existsBySurveyIdAndRespondentId[31].
• [ ] Desarrollar la aplicación Angular (frontend/) que consuma esta API.
• [ ] Añadir pruebas unitarias e integración que cubran autenticación, validaciones y lógica de negocio.
• [ ] Integrar CI/CD (GitHub Actions) y badges de estado en el README.
• [ ] Definir licencia abierta (MIT, Apache 2.0) y código de conducta.

Si detectas otras mejoras o errores, abre un issue en GitHub describiendo el problema y la posible solución.

---

## Cómo contribuir

¡Las contribuciones son bienvenidas! Sigue estos pasos para colaborar:

1. **Fork del repositorio** en tu cuenta.
2. Crea una **rama** a partir de main con un nombre descriptivo (feature/añadir-survey-endpoint).
3. Implementa tus cambios asegurándote de que el proyecto compila (./mvnw clean package) y que las bases de datos se levantan correctamente.
4. Realiza commits siguiendo, si lo deseas, la convención [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) para mejorar la legibilidad del historial.
5. Envía un **pull request** detallando la funcionalidad añadida o el bug corregido. La revisión se realizará tan pronto como sea posible.

Recuerda respetar el estilo de código: se utiliza **Lombok** para reducir el boilerplate y **MapStruct** para mapear entre entidades y DTO. Configura tu IDE para activar las anotaciones de Lombok y evitar errores de compilación.

---

## Código de conducta

Este proyecto aún no incorpora un documento de código de conducta. Se recomienda adoptar el ([Contributor Covenant](https://www.contributor-covenant.org/es/version/2/1/code_of_conduct/)) en futuras versiones para garantizar un entorno colaborativo respetuoso y seguro. Hasta entonces, pedimos a todas las personas contribuidoras que actúen con profesionalidad y consideración hacia los demás.

---

## Licencia y créditos

En el momento de redactar este documento, el repositorio **no especifica una licencia explícita**, por lo que se considera un proyecto de uso educativo o personal sin garantías[34]. Se recomienda añadir una licencia (como MIT o Apache 2.0) para facilitar la reutilización y protección legal.

El proyecto fue iniciado por [VictorAmadeu](https://github.com/VictorAmadeu). Agradecemos las contribuciones de todas las personas que participan en su desarrollo y documentación. Cualquier marca o nombre registrado citado pertenece a sus respectivos propietarios.

---

[1] [11] User.java
[https://github.com/VictorAmadeu/encuestas/blob/72cca42f0b7cfaade135ee532256c7be10198ae2/backend/src/main/java/com/acme/encuestas/model/User.java](https://github.com/VictorAmadeu/encuestas/blob/72cca42f0b7cfaade135ee532256c7be10198ae2/backend/src/main/java/com/acme/encuestas/model/User.java)

[2] [21] [22] AuthService.java
[https://github.com/VictorAmadeu/encuestas/blob/72cca42f0b7cfaade135ee532256c7be10198ae2/backend/src/main/java/com/acme/encuestas/service/AuthService.java](https://github.com/VictorAmadeu/encuestas/blob/72cca42f0b7cfaade135ee532256c7be10198ae2/backend/src/main/java/com/acme/encuestas/service/AuthService.java)

[3] [29] [30] JwtUtil.java
[https://github.com/VictorAmadeu/encuestas/blob/72cca42f0b7cfaade135ee532256c7be10198ae2/backend/src/main/java/com/acme/encuestas/security/JwtUtil.java](https://github.com/VictorAmadeu/encuestas/blob/72cca42f0b7cfaade135ee532256c7be10198ae2/backend/src/main/java/com/acme/encuestas/security/JwtUtil.java)

[4] [33] V1\_\_init.sql
[https://github.com/VictorAmadeu/encuestas/blob/72cca42f0b7cfaade135ee532256c7be10198ae2/backend/src/main/resources/db/migration/V1\_\_init.sql](https://github.com/VictorAmadeu/encuestas/blob/72cca42f0b7cfaade135ee532256c7be10198ae2/backend/src/main/resources/db/migration/V1__init.sql)

[5] ResponseDocument.java
[https://github.com/VictorAmadeu/encuestas/blob/72cca42f0b7cfaade135ee532256c7be10198ae2/backend/src/main/java/com/acme/encuestas/document/ResponseDocument.java](https://github.com/VictorAmadeu/encuestas/blob/72cca42f0b7cfaade135ee532256c7be10198ae2/backend/src/main/java/com/acme/encuestas/document/ResponseDocument.java)

[6] JwtFilter.java
[https://github.com/VictorAmadeu/encuestas/blob/72cca42f0b7cfaade135ee532256c7be10198ae2/backend/src/main/java/com/acme/encuestas/security/JwtFilter.java](https://github.com/VictorAmadeu/encuestas/blob/72cca42f0b7cfaade135ee532256c7be10198ae2/backend/src/main/java/com/acme/encuestas/security/JwtFilter.java)

[7] SecurityConfig.java
[https://github.com/VictorAmadeu/encuestas/blob/72cca42f0b7cfaade135ee532256c7be10198ae2/backend/src/main/java/com/acme/encuestas/config/SecurityConfig.java](https://github.com/VictorAmadeu/encuestas/blob/72cca42f0b7cfaade135ee532256c7be10198ae2/backend/src/main/java/com/acme/encuestas/config/SecurityConfig.java)

[8] [9] [14] [18] [19] [25] [28] application-dev.yml
[https://github.com/VictorAmadeu/encuestas/blob/72cca42f0b7cfaade135ee532256c7be10198ae2/backend/src/main/resources/application-dev.yml](https://github.com/VictorAmadeu/encuestas/blob/72cca42f0b7cfaade135ee532256c7be10198ae2/backend/src/main/resources/application-dev.yml)

[10] EncuestasApplication.java
[https://github.com/VictorAmadeu/encuestas/blob/72cca42f0b7cfaade135ee532256c7be10198ae2/backend/src/main/java/com/acme/encuestas/EncuestasApplication.java](https://github.com/VictorAmadeu/encuestas/blob/72cca42f0b7cfaade135ee532256c7be10198ae2/backend/src/main/java/com/acme/encuestas/EncuestasApplication.java)

[12] [32] Survey.java
[https://github.com/VictorAmadeu/encuestas/blob/72cca42f0b7cfaade135ee532256c7be10198ae2/backend/src/main/java/com/acme/encuestas/model/Survey.java](https://github.com/VictorAmadeu/encuestas/blob/72cca42f0b7cfaade135ee532256c7be10198ae2/backend/src/main/java/com/acme/encuestas/model/Survey.java)

[13] [20] [24] application.yml
[https://github.com/VictorAmadeu/encuestas/blob/72cca42f0b7cfaade135ee532256c7be10198ae2/backend/src/main/resources/application.yml](https://github.com/VictorAmadeu/encuestas/blob/72cca42f0b7cfaade135ee532256c7be10198ae2/backend/src/main/resources/application.yml)

[15] DevSecurityConfig.java
[https://github.com/VictorAmadeu/encuestas/blob/72cca42f0b7cfaade135ee532256c7be10198ae2/backend/src/main/java/com/acme/encuestas/config/DevSecurityConfig.java](https://github.com/VictorAmadeu/encuestas/blob/72cca42f0b7cfaade135ee532256c7be10198ae2/backend/src/main/java/com/acme/encuestas/config/DevSecurityConfig.java)

[16] [17] docker-compose.yml
[https://github.com/VictorAmadeu/encuestas/blob/72cca42f0b7cfaade135ee532256c7be10198ae2/docker-compose.yml](https://github.com/VictorAmadeu/encuestas/blob/72cca42f0b7cfaade135ee532256c7be10198ae2/docker-compose.yml)

[23] [27] pom.xml
[https://github.com/VictorAmadeu/encuestas/blob/72cca42f0b7cfaade135ee532256c7be10198ae2/backend/pom.xml](https://github.com/VictorAmadeu/encuestas/blob/72cca42f0b7cfaade135ee532256c7be10198ae2/backend/pom.xml)

[26] EncuestasApplicationTests.java
[https://github.com/VictorAmadeu/encuestas/blob/72cca42f0b7cfaade135ee532256c7be10198ae2/backend/src/test/java/com/acme/encuestas/EncuestasApplicationTests.java](https://github.com/VictorAmadeu/encuestas/blob/72cca42f0b7cfaade135ee532256c7be10198ae2/backend/src/test/java/com/acme/encuestas/EncuestasApplicationTests.java)

[31] ResponseRepository.java
[https://github.com/VictorAmadeu/encuestas/blob/72cca42f0b7cfaade135ee532256c7be10198ae2/backend/src/main/java/com/acme/encuestas/repository/ResponseRepository.java](https://github.com/VictorAmadeu/encuestas/blob/72cca42f0b7cfaade135ee532256c7be10198ae2/backend/src/main/java/com/acme/encuestas/repository/ResponseRepository.java)

[34] README.md
[https://github.com/VictorAmadeu/encuestas/blob/72cca42f0b7cfaade135ee532256c7be10198ae2/README.md](https://github.com/VictorAmadeu/encuestas/blob/72cca42f0b7cfaade135ee532256c7be10198ae2/README.md)

---
