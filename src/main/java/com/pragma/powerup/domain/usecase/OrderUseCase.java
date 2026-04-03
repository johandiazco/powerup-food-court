package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IOrderServicePort;
import com.pragma.powerup.domain.api.IOrderTraceServicePort;
import com.pragma.powerup.domain.exception.ActiveOrderExistsException;
import com.pragma.powerup.domain.exception.DishNotFoundException;
import com.pragma.powerup.domain.exception.InvalidOrderOperationException;
import com.pragma.powerup.domain.exception.OrderNotFoundException;
import com.pragma.powerup.domain.exception.RestaurantNotFoundException;
import com.pragma.powerup.domain.model.Dish;
import com.pragma.powerup.domain.model.DomainPage;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.model.PaginationParams;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.spi.IUserPersistencePort;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderUseCase implements IOrderServicePort {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int PIN_BOUND = 900000;
    private static final int PIN_BASE = 100000;

    private final IOrderPersistencePort orderPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IDishPersistencePort dishPersistencePort;
    private final IOrderTraceServicePort traceService;
    private final IUserPersistencePort userPersistencePort;

    public OrderUseCase(
            IOrderPersistencePort orderPersistencePort,
            IRestaurantPersistencePort restaurantPersistencePort,
            IDishPersistencePort dishPersistencePort,
            IOrderTraceServicePort traceService,
            IUserPersistencePort userPersistencePort) {
        this.orderPersistencePort = orderPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.dishPersistencePort = dishPersistencePort;
        this.traceService = traceService;
        this.userPersistencePort = userPersistencePort;
    }

    @Override
    public Order createOrder(Order order) {
        validateRestaurantExists(order.getRestaurantId());
        validateNoActiveOrder(order.getClientId(), order.getRestaurantId());
        order.validate();
        validateDishes(order);

        order.setDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDIENTE);
        order.setChefId(null);
        order.setSecurityPin(null);

        Order savedOrder = orderPersistencePort.saveOrder(order);
        registerTrace(savedOrder.getId(), null, OrderStatus.PENDIENTE, order.getClientId());

        return savedOrder;
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderPersistencePort.findOrderById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Pedido no encontrado"));
    }

    @Override
    public DomainPage<Order> getOrdersByRestaurantAndStatus(
            Long restaurantId, OrderStatus status, PaginationParams params) {
        return orderPersistencePort.findOrdersByRestaurantAndStatus(restaurantId, status, params);
    }

    @Override
    public void assignOrderToChef(Long orderId, Long chefId) {
        Order order = getOrderById(orderId);

        if (!order.canBeAssigned()) {
            throw new InvalidOrderOperationException(
                    "El pedido no puede ser asignado. Estado actual: " + order.getStatus());
        }

        OrderStatus previousStatus = order.getStatus();
        order.setChefId(chefId);
        order.setStatus(OrderStatus.EN_PREPARACION);
        orderPersistencePort.saveOrder(order);

        registerTrace(orderId, previousStatus, OrderStatus.EN_PREPARACION, chefId);
    }

    @Override
    public void markOrderAsReady(Long orderId, Long chefId) {
        Order order = getOrderById(orderId);

        if (!order.canBeMarkedAsReady()) {
            throw new InvalidOrderOperationException(
                    "El pedido no puede ser marcado como listo. Estado actual: " + order.getStatus());
        }

        if (!order.getChefId().equals(chefId)) {
            throw new InvalidOrderOperationException(
                    "Solo el empleado asignado puede marcar el pedido como listo");
        }

        OrderStatus previousStatus = order.getStatus();
        order.setSecurityPin(generateSecurityPin());
        order.setStatus(OrderStatus.LISTO);
        orderPersistencePort.saveOrder(order);

        registerTrace(orderId, previousStatus, OrderStatus.LISTO, chefId);
    }

    @Override
    public void deliverOrder(Long orderId, String securityPin) {
        Order order = getOrderById(orderId);

        if (!order.canBeDelivered()) {
            throw new InvalidOrderOperationException(
                    "El pedido no puede ser entregado. Estado actual: " + order.getStatus());
        }

        if (!order.getSecurityPin().equals(securityPin)) {
            throw new InvalidOrderOperationException("PIN de seguridad incorrecto");
        }

        OrderStatus previousStatus = order.getStatus();
        order.setStatus(OrderStatus.ENTREGADO);
        orderPersistencePort.saveOrder(order);

        registerTrace(orderId, previousStatus, OrderStatus.ENTREGADO, order.getChefId());
    }

    @Override
    public void cancelOrder(Long orderId, Long clientId) {
        Order order = getOrderById(orderId);

        if (!order.getClientId().equals(clientId)) {
            throw new InvalidOrderOperationException(
                    "Solo el cliente que realizó el pedido puede cancelarlo");
        }

        if (!order.canBeCancelled()) {
            throw new InvalidOrderOperationException(
                    "El pedido solo puede ser cancelado si está en estado PENDIENTE. Estado actual: " + order.getStatus());
        }

        OrderStatus previousStatus = order.getStatus();
        order.setStatus(OrderStatus.CANCELADO);
        orderPersistencePort.saveOrder(order);

        registerTrace(orderId, previousStatus, OrderStatus.CANCELADO, clientId);
    }

    private void validateRestaurantExists(Long restaurantId) {
        restaurantPersistencePort.findRestaurantById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
    }

    private void validateNoActiveOrder(Long clientId, Long restaurantId) {
        if (orderPersistencePort.existsActiveOrderByClientIdAndRestaurantId(clientId, restaurantId)) {
            throw new ActiveOrderExistsException(
                    "El cliente ya tiene un pedido activo en este restaurante");
        }
    }

    private void validateDishes(Order order) {
        Set<Long> dishIds = new HashSet<>();

        order.getDishes().forEach(orderDish -> {
            Long dishId = orderDish.getDishId();

            if (!dishIds.add(dishId)) {
                throw new IllegalArgumentException("No se permiten platos duplicados en el pedido");
            }

            Dish dish = dishPersistencePort.findDishById(dishId)
                    .orElseThrow(() -> new DishNotFoundException(dishId));

            if (Boolean.FALSE.equals(dish.getActive())) {
                throw new IllegalArgumentException("El plato no está disponible: " + dishId);
            }

            if (!dish.getRestaurantId().equals(order.getRestaurantId())) {
                throw new IllegalArgumentException(
                        "El plato " + dishId + " no pertenece al restaurante especificado");
            }
        });
    }

    private String generateSecurityPin() {
        int pin = PIN_BASE + RANDOM.nextInt(PIN_BOUND);
        return String.valueOf(pin);
    }

    private void registerTrace(Long orderId, OrderStatus previousStatus, OrderStatus newStatus, Long userId) {
        try {
            var user = userPersistencePort.findUserById(userId).orElse(null);

            traceService.logStatusChange(
                    orderId,
                    previousStatus != null ? previousStatus.name() : null,
                    newStatus.name(),
                    userId,
                    user != null ? user.getCorreo() : "SYSTEM",
                    user != null ? user.getRol().name() : "SYSTEM"
            );
        } catch (Exception e) {
            log.warn("Error al registrar trazabilidad para pedido {}: {}", orderId, e.getMessage());
        }
    }
}