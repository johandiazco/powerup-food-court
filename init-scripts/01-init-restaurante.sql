-- Conectar a la base de datos
\c powerup_pragma;

CREATE TABLE IF NOT EXISTS categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS restaurants (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    nit VARCHAR(20) NOT NULL UNIQUE,
    address VARCHAR(300) NOT NULL,
    phone VARCHAR(13) NOT NULL,
    url_logo TEXT,
    owner_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_phone_valid CHECK (telefono ~ '^\+?[0-9]{10,13}$')
    );

CREATE TABLE IF NOT EXISTS dishes (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    price DECIMAL(12,2) NOT NULL,
    url_image TEXT,
    active BOOLEAN DEFAULT TRUE,
    categories_id BIGINT NOT NULL,
    restaurants_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_plato_categories FOREIGN KEY (categories_id)
        REFERENCES categories(id),
    CONSTRAINT fk_dishes_restaurants FOREIGN KEY (restaurants_id)
        REFERENCES restaurants(id) ON DELETE CASCADE,
    CONSTRAINT chk_price_positive CHECK (price > 0)
    );

CREATE INDEX idx_dishes_restaurants ON platos(restaurants_id);
CREATE INDEX idx_dishes_active ON platos(active);

INSERT INTO categories (name, description) VALUES
    ('Entradas', 'Platos de entrada'),
    ('Platos Fuertes', 'Platos principales'),
    ('Bebidas', 'Bebidas'),
    ('Postres', 'Postres')
    ON CONFLICT (name) DO NOTHING;