
CREATE DATABASE DATOS;

USE DATOS;

-- Crear tabla PAISES
CREATE TABLE PAISES (
    pais_ID INT IDENTITY(1,1) PRIMARY KEY,
    pais_nombre NVARCHAR(100) NOT NULL
);

-- Crear tabla CIUDADES
CREATE TABLE CIUDADES (
    ciud_ID INT IDENTITY(1,1) PRIMARY KEY,
    ciud_nombre NVARCHAR(100) NOT NULL,
    ciud_pais_ID INT,
    CONSTRAINT fk_ciudades_paises FOREIGN KEY (ciud_pais_ID) REFERENCES PAISES(pais_ID)
);

-- Crear tabla LOCALIZACIONES
CREATE TABLE LOCALIZACIONES (
    localiz_ID INT IDENTITY(1,1)  PRIMARY KEY,
    localiz_direccion NVARCHAR(200) NOT NULL,
    localiz_ciudad_ID INT,
    CONSTRAINT fk_localizaciones_ciudades FOREIGN KEY (localiz_ciudad_ID) REFERENCES CIUDADES(ciud_ID)
);

-- Crear tabla DEPARTAMENTOS (relación con LOCALIZACIONES)
CREATE TABLE DEPARTAMENTOS (
    dpto_ID INT IDENTITY(1,1) PRIMARY KEY,
    dpto_nombre NVARCHAR(100) NOT NULL,
    dpto_localiz_ID INT,
    CONSTRAINT fk_departamentos_localizaciones FOREIGN KEY (dpto_localiz_ID) REFERENCES LOCALIZACIONES(localiz_ID)
);

-- Crear tabla CARGOS
CREATE TABLE CARGOS (
    cargo_ID INT IDENTITY(1,1) PRIMARY KEY,
    cargo_nombre NVARCHAR(100) NOT NULL,
    cargo_sueldo_minimo DECIMAL(18, 2) NOT NULL,
    cargo_sueldo_maximo DECIMAL(18, 2) NOT NULL
);

-- Crear tabla EMPLEADOS
CREATE TABLE EMPLEADOS (
    empl_ID INT IDENTITY(1,1) PRIMARY KEY,
    empl_nombre NVARCHAR(50) NOT NULL,
    empl_apellido NVARCHAR(50),
    empl_email NVARCHAR(100) NOT NULL,
    empl_fecha_nac DATE NOT NULL,
    empl_sueldo DECIMAL(18, 2) NOT NULL,
    empl_comision DECIMAL(18, 2),
    empl_cargo_ID INT,
    empl_Gerente_ID INT,
    empl_dpto_ID INT,
	empl_estado NVARCHAR(50) NOT NULL
    CONSTRAINT fk_empleados_cargos FOREIGN KEY (empl_cargo_ID) REFERENCES CARGOS(cargo_ID),
    CONSTRAINT fk_empleados_gerentes FOREIGN KEY (empl_Gerente_ID) REFERENCES EMPLEADOS(empl_ID),
    CONSTRAINT fk_empleados_departamentos FOREIGN KEY (empl_dpto_ID) REFERENCES DEPARTAMENTOS(dpto_ID)
);

-- Crear tabla HISTORICO (con relación a EMPLEADOS)
CREATE TABLE HISTORICO (
    emphist_ID INT IDENTITY(1,1) PRIMARY KEY, -- Clave primaria
    emphist_fecha_retiro DATE NOT NULL,
    emphist_cargo_ID INT, -- Clave foránea hacia CARGOS
    emphist_dpto_ID INT, -- Clave foránea hacia DEPARTAMENTOS
    emphist_empleado_ID INT, -- Clave foránea hacia EMPLEADOS
    CONSTRAINT fk_historico_cargos FOREIGN KEY (emphist_cargo_ID) REFERENCES CARGOS(cargo_ID),
    CONSTRAINT fk_historico_departamentos FOREIGN KEY (emphist_dpto_ID) REFERENCES DEPARTAMENTOS(dpto_ID),
    CONSTRAINT fk_historico_empleados FOREIGN KEY (emphist_empleado_ID) REFERENCES EMPLEADOS(empl_ID)
);

-- Insertar registros en la tabla PAISES
INSERT INTO PAISES (pais_nombre) VALUES ('Colombia');
INSERT INTO PAISES (pais_nombre) VALUES ('México');

-- Insertar registros en la tabla CIUDADES
INSERT INTO CIUDADES (ciud_nombre, ciud_pais_ID) VALUES ('Bogotá', 1);
INSERT INTO CIUDADES (ciud_nombre, ciud_pais_ID) VALUES ('Medellín', 1);
INSERT INTO CIUDADES (ciud_nombre, ciud_pais_ID) VALUES ('Ciudad de México', 2);

-- Insertar registros en la tabla LOCALIZACIONES
INSERT INTO LOCALIZACIONES (localiz_direccion, localiz_ciudad_ID) VALUES ('Carrera 7 # 24-32', 1);
INSERT INTO LOCALIZACIONES (localiz_direccion, localiz_ciudad_ID) VALUES ('Avenida Poblado 65', 2);
INSERT INTO LOCALIZACIONES (localiz_direccion, localiz_ciudad_ID) VALUES ('Calle Reforma 100', 3);

-- Insertar registros en la tabla DEPARTAMENTOS
INSERT INTO DEPARTAMENTOS (dpto_nombre, dpto_localiz_ID) VALUES ('Ventas', 1);
INSERT INTO DEPARTAMENTOS (dpto_nombre, dpto_localiz_ID) VALUES ('Marketing', 2);
INSERT INTO DEPARTAMENTOS (dpto_nombre, dpto_localiz_ID) VALUES ('Finanzas', 3);

-- Insertar registros en la tabla CARGOS
INSERT INTO CARGOS (cargo_nombre, cargo_sueldo_minimo, cargo_sueldo_maximo) VALUES ('Gerente de Ventas', 5000000, 12000000);
INSERT INTO CARGOS (cargo_nombre, cargo_sueldo_minimo, cargo_sueldo_maximo) VALUES ('Asistente de Marketing', 2000000, 4000000);
INSERT INTO CARGOS (cargo_nombre, cargo_sueldo_minimo, cargo_sueldo_maximo) VALUES ('Analista de Finanzas', 3000000, 7000000);

-- Insertar registros en la tabla EMPLEADOS
-- Asumiendo que el Gerente de Ventas no tiene un gerente, por eso se asigna NULL en 'empl_Gerente_ID'
INSERT INTO EMPLEADOS (empl_nombre, empl_apellido, empl_email, empl_fecha_nac, empl_sueldo, empl_comision, empl_cargo_ID, empl_Gerente_ID, empl_dpto_ID, empl_estado) 
VALUES ('Carlos', 'Gómez', 'carlos.gomez@empresa.com', '1980-05-15', 8000000, 200000, 1, NULL, 1, 'ACTIVO');

-- Asignar un gerente para el asistente de marketing
INSERT INTO EMPLEADOS (empl_nombre, empl_apellido, empl_email, empl_fecha_nac, empl_sueldo, empl_comision, empl_cargo_ID, empl_Gerente_ID, empl_dpto_ID, empl_estado)
VALUES ('Ana', 'Martínez', 'ana.martinez@empresa.com', '1990-10-10', 3000000, 0, 2, 1, 2, 'ACTIVO');

-- Insertar registros en la tabla HISTORICO (Empleados que ya no trabajan)
-- Empleado con ID 2 (Ana Martínez) ha sido retirada del cargo de Asistente de Marketing
INSERT INTO HISTORICO (emphist_fecha_retiro, emphist_cargo_ID, emphist_dpto_ID, emphist_empleado_ID)
VALUES ('2024-11-10', 2, 2, 2);

-- Consultas Select
SELECT * FROM PAISES;
SELECT * FROM CIUDADES;
SELECT * FROM LOCALIZACIONES;
SELECT * FROM DEPARTAMENTOS;
SELECT * FROM CARGOS;
SELECT * FROM EMPLEADOS;
SELECT * FROM HISTORICO;


/*
-- Eliminar las restricciones de claves foráneas antes de borrar las tablas
ALTER TABLE HISTORICO DROP CONSTRAINT fk_historico_empleados;
ALTER TABLE HISTORICO DROP CONSTRAINT fk_historico_departamentos;
ALTER TABLE HISTORICO DROP CONSTRAINT fk_historico_cargos;

ALTER TABLE EMPLEADOS DROP CONSTRAINT fk_empleados_departamentos;
ALTER TABLE EMPLEADOS DROP CONSTRAINT fk_empleados_gerentes;
ALTER TABLE EMPLEADOS DROP CONSTRAINT fk_empleados_cargos;

ALTER TABLE DEPARTAMENTOS DROP CONSTRAINT fk_departamentos_localizaciones;
ALTER TABLE LOCALIZACIONES DROP CONSTRAINT fk_localizaciones_ciudades;
ALTER TABLE CIUDADES DROP CONSTRAINT fk_ciudades_paises;

-- Borrar las tablas
DROP TABLE HISTORICO;
DROP TABLE EMPLEADOS;
DROP TABLE DEPARTAMENTOS;
DROP TABLE CARGOS;
DROP TABLE LOCALIZACIONES;
DROP TABLE CIUDADES;
DROP TABLE PAISES;

*/
