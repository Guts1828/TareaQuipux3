-- Script MySQL de referencia para la base de datos de la UCO.
-- NOTA: no es obligatorio ejecutarlo; Spring Boot (ddl-auto=update) crea las tablas solo.

CREATE DATABASE IF NOT EXISTS uco_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE uco_db;

CREATE TABLE IF NOT EXISTS usuarios (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    email     VARCHAR(120) NOT NULL UNIQUE,
    password  VARCHAR(255) NOT NULL,
    rol       VARCHAR(20)  NOT NULL,           -- ESTUDIANTE | PROFESOR
    activo    BIT(1)       NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS profesores (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombres       VARCHAR(80)  NOT NULL,
    apellidos     VARCHAR(80)  NOT NULL,
    departamento  VARCHAR(100) NOT NULL,
    usuario_id    BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_profesor_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE IF NOT EXISTS estudiantes (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo      VARCHAR(20)  NOT NULL UNIQUE,
    nombres     VARCHAR(80)  NOT NULL,
    apellidos   VARCHAR(80)  NOT NULL,
    programa    VARCHAR(100) NOT NULL,
    semestre    INT          NOT NULL,
    usuario_id  BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_estudiante_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE IF NOT EXISTS cursos (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo       VARCHAR(20)  NOT NULL UNIQUE,
    nombre       VARCHAR(120) NOT NULL,
    creditos     INT          NOT NULL,
    periodo      VARCHAR(10)  NOT NULL,
    profesor_id  BIGINT NOT NULL,
    CONSTRAINT fk_curso_profesor FOREIGN KEY (profesor_id) REFERENCES profesores(id)
);

CREATE TABLE IF NOT EXISTS matriculas (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    estudiante_id  BIGINT NOT NULL,
    curso_id       BIGINT NOT NULL,
    fecha          DATE   NOT NULL,
    CONSTRAINT uq_matricula UNIQUE (estudiante_id, curso_id),
    CONSTRAINT fk_matricula_estudiante FOREIGN KEY (estudiante_id) REFERENCES estudiantes(id),
    CONSTRAINT fk_matricula_curso FOREIGN KEY (curso_id) REFERENCES cursos(id)
);

CREATE TABLE IF NOT EXISTS calificaciones (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    matricula_id  BIGINT        NOT NULL,
    descripcion   VARCHAR(100)  NOT NULL,
    nota          DECIMAL(3,2)  NOT NULL,      -- 0.00 a 5.00
    porcentaje    DECIMAL(5,2)  NOT NULL,      -- 0.01 a 100.00
    observacion   VARCHAR(500),
    fecha         DATE          NOT NULL,
    CONSTRAINT fk_calificacion_matricula FOREIGN KEY (matricula_id) REFERENCES matriculas(id)
);
