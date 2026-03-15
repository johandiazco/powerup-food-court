package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface IOrderPersistencePort {
    Order saveOrder(Order order);
    Optional<Order> findOrderById(Long orderId);
    Page<Order> findOrdersByRestaurantAndStatus(Long restaurantId, OrderStatus status, Pageable pageable);
    boolean existsActiveOrderByClientIdAndRestaurantId(Long clientId, Long restaurantId);
}