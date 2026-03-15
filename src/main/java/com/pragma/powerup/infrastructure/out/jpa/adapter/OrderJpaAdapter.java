package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.infrastructure.out.jpa.entity.OrderEntity;
import com.pragma.powerup.infrastructure.out.jpa.entity.OrderStatusEntity;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IOrderEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderJpaAdapter implements IOrderPersistencePort {

    private final IOrderRepository orderRepository;
    private final IOrderEntityMapper orderEntityMapper;

    @Override
    public Order saveOrder(Order order) {
        OrderEntity orderEntity = orderEntityMapper.toEntity(order);

        //Establecemos relación bidireccional con los platos
        if (orderEntity.getDishes() != null) {
            orderEntity.getDishes().forEach(dish -> dish.setOrder(orderEntity));
        }

        OrderEntity savedEntity = orderRepository.save(orderEntity);
        return orderEntityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Order> findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .map(orderEntityMapper::toDomain);
    }

    @Override
    public Page<Order> findOrdersByRestaurantAndStatus(
            Long restaurantId,
            OrderStatus status,
            Pageable pageable) {
        OrderStatusEntity statusEntity = OrderStatusEntity.valueOf(status.name());

        Page<OrderEntity> entities = orderRepository.findByRestaurantIdAndStatus(
                restaurantId, statusEntity, pageable);

        return entities.map(orderEntityMapper::toDomain);
    }

    @Override
    public boolean existsActiveOrderByClientIdAndRestaurantId(
            Long clientId,
            Long restaurantId) {
        return orderRepository.existsActiveOrderByClientIdAndRestaurantId(
                clientId, restaurantId);
    }
}