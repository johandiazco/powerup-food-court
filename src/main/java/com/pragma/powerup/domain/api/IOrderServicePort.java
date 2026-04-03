package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.DomainPage;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.model.PaginationParams;

public interface IOrderServicePort {
    Order createOrder(Order order);
    Order getOrderById(Long orderId);
    DomainPage<Order> getOrdersByRestaurantAndStatus(Long restaurantId, OrderStatus status, PaginationParams params);
    void assignOrderToChef(Long orderId, Long chefId);
    void markOrderAsReady(Long orderId, Long chefId);
    void deliverOrder(Long orderId, String securityPin);
    void cancelOrder(Long orderId, Long clientId);
}