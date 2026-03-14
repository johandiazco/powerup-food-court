\c powerup_pragma;

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    documento_identidad VARCHAR(20) NOT NULL UNIQUE,
    celular VARCHAR(13) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    clave VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_celular_valid CHECK (celular ~ '^\+?[0-9]{10,13}$'),
    CONSTRAINT chk_rol_valid CHECK (rol IN ('ADMIN', 'PROPIETARIO', 'EMPLEADO', 'CLIENTE'))
    );

CREATE INDEX idx_users_correo ON users(correo);
CREATE INDEX idx_users_documento ON users(documento_identidad);
CREATE INDEX idx_users_rol ON users(rol);

--Usuario ADMIN; contraseña: admin123
--BCrypt hash de "admin123"
INSERT INTO users (nombre, apellido, documento_identidad, celular, fecha_nacimiento, correo, clave, rol)
VALUES (
           'Administrador',
           'Sistema',
           '1234567890',
           '+573001234567',
           '1990-01-01',
           'admin@plazoleta.com',
           '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzxRGfqLLBeYt6lKTk0FfPzpJVW0.',
           'ADMIN'
       ) ON CONFLICT (correo) DO NOTHING;