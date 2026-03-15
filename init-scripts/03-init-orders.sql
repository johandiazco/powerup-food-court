-- tabla de pedidos
CREATE TABLE IF NOT EXISTS orders (
                                      id BIGSERIAL PRIMARY KEY,
                                      client_id BIGINT NOT NULL,
                                      date TIMESTAMP NOT NULL,
                                      status VARCHAR(20) NOT NULL CHECK (status IN ('PENDIENTE', 'EN_PREPARACION', 'LISTO', 'ENTREGADO', 'CANCELADO')),
    chef_id BIGINT,
    restaurant_id BIGINT NOT NULL,
    security_pin VARCHAR(10),
    CONSTRAINT fk_orders_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
    );

-- tabla de platos por pedido
CREATE TABLE IF NOT EXISTS order_dishes (
                                            id BIGSERIAL PRIMARY KEY,
                                            order_id BIGINT NOT NULL,
                                            dish_id BIGINT NOT NULL,
                                            quantity INTEGER NOT NULL CHECK (quantity > 0),
    CONSTRAINT fk_order_dishes_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_dishes_dish FOREIGN KEY (dish_id) REFERENCES dishes(id)
    );

CREATE INDEX IF NOT EXISTS idx_orders_client_id ON orders(client_id);
CREATE INDEX IF NOT EXISTS idx_orders_restaurant_id ON orders(restaurant_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_orders_restaurant_status ON orders(restaurant_id, status);
CREATE INDEX IF NOT EXISTS idx_orders_chef_id ON orders(chef_id);
CREATE INDEX IF NOT EXISTS idx_order_dishes_order_id ON order_dishes(order_id);
CREATE INDEX IF NOT EXISTS idx_order_dishes_dish_id ON order_dishes(dish_id);

CREATE INDEX IF NOT EXISTS idx_orders_client_restaurant_status
    ON orders(client_id, restaurant_id, status);

COMMENT ON TABLE orders IS 'Tabla de pedidos - HU-11 a HU-16';
COMMENT ON TABLE order_dishes IS 'Tabla de platos por pedido';
COMMENT ON COLUMN orders.status IS 'Estados posibles: PENDIENTE, EN_PREPARACION, LISTO, ENTREGADO, CANCELADO';
COMMENT ON COLUMN orders.security_pin IS 'PIN de 6 dígitos para validar entrega - HU-14';
COMMENT ON COLUMN orders.chef_id IS 'ID del empleado asignado al pedido - HU-13';