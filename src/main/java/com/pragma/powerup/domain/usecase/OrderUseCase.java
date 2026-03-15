package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IOrderServicePort;
import com.pragma.powerup.domain.exception.ActiveOrderExistsException;
import com.pragma.powerup.domain.exception.DishNotFoundException;
import com.pragma.powerup.domain.exception.InvalidOrderOperationException;
import com.pragma.powerup.domain.exception.OrderNotFoundException;
import com.pragma.powerup.domain.exception.RestaurantNotFoundException;
import com.pragma.powerup.domain.model.Dish;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class OrderUseCase implements IOrderServicePort {

    private final IOrderPersistencePort orderPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IDishPersistencePort dishPersistencePort;

    public OrderUseCase(
            IOrderPersistencePort orderPersistencePort,
            IRestaurantPersistencePort restaurantPersistencePort,
            IDishPersistencePort dishPersistencePort
    ) {
        this.orderPersistencePort = orderPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.dishPersistencePort = dishPersistencePort;
    }

    @Override
    public Order createOrder(Order order) {
        //Validamos que el restaurante exista
        restaurantPersistencePort.findRestaurantById(order.getRestaurantId())
                .orElseThrow(() -> new RestaurantNotFoundException(order.getRestaurantId()));

        //Validamos que el cliente no tenga pedidos activos en este restaurante
        if (orderPersistencePort.existsActiveOrderByClientIdAndRestaurantId(
                order.getClientId(), order.getRestaurantId())) {
            throw new ActiveOrderExistsException(
                    "El cliente ya tiene un pedido activo en este restaurante");
        }

        //Validamos el pedido completo
        order.validate();

        //Validamos que todos los platos existan, esten activos y pertenescan al restaurante
        validateDishes(order);

        //Establecemos valores por defecto
        order.setDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDIENTE);
        order.setChefId(null);
        order.setSecurityPin(null);

        //Se guardar pedido
        return orderPersistencePort.saveOrder(order);
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderPersistencePort.findOrderById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Pedido no encontrado"));
    }

    @Override
    public Page<Order> getOrdersByRestaurantAndStatus(
            Long restaurantId, OrderStatus status, Pageable pageable) {
        //Listamos pedidos por restaurante y estado con paginación - hu12
        return orderPersistencePort.findOrdersByRestaurantAndStatus(
                restaurantId, status, pageable);
    }

    @Override
    public void assignOrderToChef(Long orderId, Long chefId) {
        //Asignamos pedido a empleado - hu13
        Order order = getOrderById(orderId);

        if (!order.canBeAssigned()) {
            throw new InvalidOrderOperationException(
                    "El pedido no puede ser asignado. Estado actual: " + order.getStatus());
        }

        order.setChefId(chefId);
        order.setStatus(OrderStatus.EN_PREPARACION);

        orderPersistencePort.saveOrder(order);
    }

    @Override
    public void markOrderAsReady(Long orderId, Long chefId) {
        //Notificamos pedido listo - hu14
        Order order = getOrderById(orderId);

        if (!order.canBeMarkedAsReady()) {
            throw new InvalidOrderOperationException(
                    "El pedido no puede ser marcado como listo. Estado actual: " + order.getStatus());
        }

        if (!order.getChefId().equals(chefId)) {
            throw new InvalidOrderOperationException(
                    "Solo el empleado asignado puede marcar el pedido como listo");
        }

        //Generamos PIN de seguridad - hu14
        String securityPin = generateSecurityPin();
        order.setSecurityPin(securityPin);
        order.setStatus(OrderStatus.LISTO);

        orderPersistencePort.saveOrder(order);
    }

    @Override
    public void deliverOrder(Long orderId, String securityPin) {
        //Entregamos pedido - hu15
        Order order = getOrderById(orderId);

        if (!order.canBeDelivered()) {
            throw new InvalidOrderOperationException(
                    "El pedido no puede ser entregado. Estado actual: " + order.getStatus());
        }

        if (!order.getSecurityPin().equals(securityPin)) {
            throw new InvalidOrderOperationException("PIN de seguridad incorrecto");
        }

        order.setStatus(OrderStatus.ENTREGADO);

        orderPersistencePort.saveOrder(order);
    }

    @Override
    public void cancelOrder(Long orderId, Long clientId) {
        //Cancelamos pedido - hu16
        Order order = getOrderById(orderId);

        if (!order.getClientId().equals(clientId)) {
            throw new InvalidOrderOperationException(
                    "Solo el cliente que realizó el pedido puede cancelarlo");
        }

        if (!order.canBeCancelled()) {
            throw new InvalidOrderOperationException(
                    "El pedido solo puede ser cancelado si está en estado PENDIENTE. Estado actual: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELADO);

        orderPersistencePort.saveOrder(order);
    }

    private void validateDishes(Order order) {
        Set<Long> dishIds = new HashSet<>();

        order.getDishes().forEach(orderDish -> {
            Long dishId = orderDish.getDishId();

            //Se verifican duplicados
            if (!dishIds.add(dishId)) {
                throw new IllegalArgumentException("No se permiten platos duplicados en el pedido");
            }

            //Obtenemos el plato
            Dish dish = dishPersistencePort.findDishById(dishId)
                    .orElseThrow(() -> new DishNotFoundException(dishId));

            //Se verificar que el plato esté activo
            if (Boolean.FALSE.equals(dish.getActive())) {
                throw new IllegalArgumentException("El plato no está disponible: " + dishId);
            }

            //Verificamos que el plato pertenezca al restaurante
            if (!dish.getRestaurantId().equals(order.getRestaurantId())) {
                throw new IllegalArgumentException(
                        "El plato " + dishId + " no pertenece al restaurante especificado");
            }
        });
    }

    private String generateSecurityPin() {
        Random random = new Random();
        int pin = 100000 + random.nextInt(900000);
        return String.valueOf(pin);
    }
}