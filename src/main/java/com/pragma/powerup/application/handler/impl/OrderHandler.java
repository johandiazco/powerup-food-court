package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.OrderRequestDto;
import com.pragma.powerup.application.dto.response.OrderResponseDto;
import com.pragma.powerup.application.handler.IOrderHandler;
import com.pragma.powerup.application.mapper.IOrderRequestMapper;
import com.pragma.powerup.application.mapper.IOrderResponseMapper;
import com.pragma.powerup.domain.api.IOrderServicePort;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderHandler implements IOrderHandler {

    private final IOrderServicePort orderServicePort;
    private final IOrderRequestMapper orderRequestMapper;
    private final IOrderResponseMapper orderResponseMapper;

    @Override
    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto, Long clientId) {
        Order order = orderRequestMapper.toOrder(orderRequestDto);
        order.setClientId(clientId);

        Order savedOrder = orderServicePort.createOrder(order);

        return orderResponseMapper.toOrderResponseDto(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(Long orderId) {
        Order order = orderServicePort.getOrderById(orderId);
        return orderResponseMapper.toOrderResponseDto(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getOrdersByRestaurantAndStatus(
            Long restaurantId, String status, Pageable pageable) {
        OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
        Page<Order> orders = orderServicePort.getOrdersByRestaurantAndStatus(
                restaurantId, orderStatus, pageable);

        return orders.map(orderResponseMapper::toOrderResponseDto);
    }

    @Override
    public void assignOrderToChef(Long orderId, Long chefId) {
        orderServicePort.assignOrderToChef(orderId, chefId);
    }

    @Override
    public void markOrderAsReady(Long orderId, Long chefId) {
        orderServicePort.markOrderAsReady(orderId, chefId);
    }

    @Override
    public void deliverOrder(Long orderId, String securityPin) {
        orderServicePort.deliverOrder(orderId, securityPin);
    }

    @Override
    public void cancelOrder(Long orderId, Long clientId) {
        orderServicePort.cancelOrder(orderId, clientId);
    }
}