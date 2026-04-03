package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.OrderRequestDto;
import com.pragma.powerup.application.dto.response.OrderResponseDto;
import com.pragma.powerup.application.dto.response.PageableResponseDto;

public interface IOrderHandler {
    OrderResponseDto createOrder(OrderRequestDto orderRequestDto, Long clientId);
    OrderResponseDto getOrderById(Long orderId);
    PageableResponseDto<OrderResponseDto> getOrdersByRestaurantAndStatus(
            Long restaurantId, String status, int page, int size);
    void assignOrderToChef(Long orderId, Long chefId);
    void markOrderAsReady(Long orderId, Long chefId);
    void deliverOrder(Long orderId, String securityPin);
    void cancelOrder(Long orderId, Long clientId);
}