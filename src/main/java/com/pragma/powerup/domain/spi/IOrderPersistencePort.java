package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.DomainPage;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.model.PaginationParams;

import java.util.Optional;

public interface IOrderPersistencePort {
    Order saveOrder(Order order);
    Optional<Order> findOrderById(Long orderId);
    DomainPage<Order> findOrdersByRestaurantAndStatus(Long restaurantId, OrderStatus status, PaginationParams params);
    boolean existsActiveOrderByClientIdAndRestaurantId(Long clientId, Long restaurantId);
}