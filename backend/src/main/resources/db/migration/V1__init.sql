-- Esquema inicial para la plataforma de encuestas (PostgreSQL)
-- Comentario: indica que este archivo crea las tablas iniciales y los índices necesarios.
--
CREATE TABLE users (  -- Inicio de la definición de la tabla 'users' que almacena los usuarios del sistema
                       id UUID PRIMARY KEY,  -- Identificador único del usuario, tipo UUID y clave primaria
                       email VARCHAR(255) UNIQUE NOT NULL,  -- Correo electrónico del usuario, debe ser único y no nulo
                       password_hash VARCHAR(255) NOT NULL,  -- Hash de la contraseña (no almacenar contraseñas en texto claro)
                       role VARCHAR(20) NOT NULL,  -- Rol del usuario (por ejemplo: ADMIN, USER), no puede ser nulo
                       enabled BOOLEAN NOT NULL DEFAULT true,  -- Indica si la cuenta está activa; por defecto true
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- Marca temporal de creación con valor por defecto
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP  -- Marca temporal de última actualización con valor por defecto
);  -- Fin de la definición de la tabla 'users'

-- Línea en blanco para separación entre definiciones de tablas

CREATE TABLE surveys (  -- Inicio de la definición de la tabla 'surveys' que guarda encuestas
                         id UUID PRIMARY KEY,  -- Identificador único de la encuesta, tipo UUID y clave primaria
                         owner_id UUID NOT NULL REFERENCES users(id),  -- FK hacia 'users.id' indicando el propietario de la encuesta
                         title VARCHAR(200) NOT NULL,  -- Título de la encuesta, obligatorio
                         description TEXT,  -- Descripción opcional de la encuesta
                         status VARCHAR(20) NOT NULL,  -- Estado de la encuesta (por ejemplo: DRAFT, PUBLISHED), obligatorio
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- Fecha de creación con valor por defecto
                         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP  -- Fecha de última actualización con valor por defecto
);  -- Fin de la definición de la tabla 'surveys'

-- Línea en blanco para separación entre definiciones de tablas

CREATE TABLE questions (  -- Inicio de la definición de la tabla 'questions' que contiene las preguntas de cada encuesta
                           id UUID PRIMARY KEY,  -- Identificador único de la pregunta, tipo UUID y clave primaria
                           survey_id UUID NOT NULL REFERENCES surveys(id),  -- FK hacia 'surveys.id' relacionando la pregunta con su encuesta
                           type VARCHAR(10) NOT NULL,  -- Tipo de pregunta (por ejemplo: text, choice), obligatorio
                           text TEXT NOT NULL,  -- Texto de la pregunta, obligatorio
                           required BOOLEAN NOT NULL DEFAULT true,  -- Indica si la respuesta es obligatoria; por defecto true
                           order_idx INT NOT NULL  -- Índice que determina el orden de la pregunta dentro de la encuesta
);  -- Fin de la definición de la tabla 'questions'

-- Línea en blanco para separación entre definiciones de tablas

CREATE TABLE options (  -- Inicio de la definición de la tabla 'options' que almacena las opciones para preguntas de tipo elección
                         id UUID PRIMARY KEY,  -- Identificador único de la opción, tipo UUID y clave primaria
                         question_id UUID NOT NULL REFERENCES questions(id),  -- FK hacia 'questions.id' relacionando la opción con su pregunta
                         text TEXT NOT NULL,  -- Texto de la opción, obligatorio
                         order_idx INT NOT NULL  -- Índice que determina el orden de la opción dentro de la pregunta
);  -- Fin de la definición de la tabla 'options'

-- Línea en blanco antes de crear índices para mejorar legibilidad

CREATE UNIQUE INDEX ux_users_email ON users(email);  -- Índice único para acelerar búsquedas por email y garantizar unicidad
CREATE INDEX ix_survey_owner ON surveys(owner_id);  -- Índice para acelerar consultas que filtran encuestas por propietario
CREATE INDEX ix_question_survey ON questions(survey_id);  -- Índice para acelerar consultas que obtienen preguntas de una encuesta
CREATE INDEX ix_option_question ON options(question_id);  -- Índice para acelerar consultas que obtienen opciones de una pregunta
