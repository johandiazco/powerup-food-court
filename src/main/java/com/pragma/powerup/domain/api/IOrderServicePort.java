package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IOrderServicePort {
    Order createOrder(Order order);
    Order getOrderById(Long orderId);
    Page<Order> getOrdersByRestaurantAndStatus(Long restaurantId, OrderStatus status, Pageable pageable);
    void assignOrderToChef(Long orderId, Long chefId);
    void markOrderAsReady(Long orderId, Long chefId);
    void deliverOrder(Long orderId, String securityPin);
    void cancelOrder(Long orderId, Long clientId);
}