package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.OrderRequestDto;
import com.pragma.powerup.application.dto.response.OrderResponseDto;
import com.pragma.powerup.application.dto.response.PageableResponseDto;
import com.pragma.powerup.application.handler.IOrderHandler;
import com.pragma.powerup.application.mapper.IOrderRequestMapper;
import com.pragma.powerup.application.mapper.IOrderResponseMapper;
import com.pragma.powerup.domain.api.IOrderServicePort;
import com.pragma.powerup.domain.model.DomainPage;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.model.PaginationParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
    public PageableResponseDto<OrderResponseDto> getOrdersByRestaurantAndStatus(
            Long restaurantId, String status, int page, int size) {

        OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
        PaginationParams params = new PaginationParams(page, size);

        DomainPage<Order> orderPage = orderServicePort.getOrdersByRestaurantAndStatus(
                restaurantId, orderStatus, params);

        List<OrderResponseDto> orderDtos = orderPage.getContent().stream()
                .map(orderResponseMapper::toOrderResponseDto)
                .collect(Collectors.toList());

        PageableResponseDto<OrderResponseDto> response = new PageableResponseDto<>();
        response.setContent(orderDtos);
        response.setPageNumber(orderPage.getPageNumber());
        response.setPageSize(orderPage.getPageSize());
        response.setTotalElements(orderPage.getTotalElements());
        response.setTotalPages(orderPage.getTotalPages());
        response.setFirst(orderPage.isFirst());
        response.setLast(orderPage.isLast());
        response.setEmpty(orderPage.isEmpty());

        return response;
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