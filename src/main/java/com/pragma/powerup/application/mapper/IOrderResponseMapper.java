package com.pragma.powerup.application.mapper;

import com.pragma.powerup.application.dto.response.OrderDishResponseDto;
import com.pragma.powerup.application.dto.response.OrderResponseDto;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderDish;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IOrderResponseMapper {

    @Mapping(target = "status", expression = "java(order.getStatus().name())")
    OrderResponseDto toOrderResponseDto(Order order);
    OrderDishResponseDto toOrderDishResponseDto(OrderDish orderDish);
}