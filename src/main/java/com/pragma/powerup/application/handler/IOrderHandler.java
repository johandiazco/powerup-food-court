package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.OrderRequestDto;
import com.pragma.powerup.application.dto.response.OrderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IOrderHandler {
    OrderResponseDto createOrder(OrderRequestDto orderRequestDto, Long clientId);
    OrderResponseDto getOrderById(Long orderId);
    Page<OrderResponseDto> getOrdersByRestaurantAndStatus(Long restaurantId, String status, Pageable pageable);
    void assignOrderToChef(Long orderId, Long chefId);
    void markOrderAsReady(Long orderId, Long chefId);
    void deliverOrder(Long orderId, String securityPin);
    void cancelOrder(Long orderId, Long clientId);
}