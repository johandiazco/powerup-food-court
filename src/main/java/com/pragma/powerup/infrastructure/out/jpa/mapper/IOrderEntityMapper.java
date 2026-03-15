package com.pragma.powerup.infrastructure.out.jpa.mapper;

import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderDish;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.infrastructure.out.jpa.entity.OrderDishEntity;
import com.pragma.powerup.infrastructure.out.jpa.entity.OrderEntity;
import com.pragma.powerup.infrastructure.out.jpa.entity.OrderStatusEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IOrderEntityMapper {

    @Mapping(target = "status", qualifiedByName = "statusToEntity")
    OrderEntity toEntity(Order order);

    @Mapping(target = "status", qualifiedByName = "statusToDomain")
    Order toDomain(OrderEntity orderEntity);

    @Mapping(target = "order", ignore = true)
    OrderDishEntity toEntity(OrderDish orderDish);

    OrderDish toDomain(OrderDishEntity orderDishEntity);

    @Named("statusToEntity")
    default OrderStatusEntity statusToEntity(OrderStatus status) {
        if (status == null) {
            return null;
        }
        return OrderStatusEntity.valueOf(status.name());
    }

    @Named("statusToDomain")
    default OrderStatus statusToDomain(OrderStatusEntity status) {
        if (status == null) {
            return null;
        }
        return OrderStatus.valueOf(status.name());
    }
}