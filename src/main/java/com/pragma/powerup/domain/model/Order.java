package com.pragma.powerup.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private Long id;
    private Long clientId;
    private LocalDateTime date;
    private OrderStatus status;
    private Long chefId;
    private Long restaurantId;
    private String securityPin;

    @Builder.Default
    private List<OrderDish> dishes = new ArrayList<>();

    public void validate() {
        if (clientId == null) {
            throw new IllegalArgumentException("El ID del cliente es obligatorio");
        }

        if (restaurantId == null) {
            throw new IllegalArgumentException("El ID del restaurante es obligatorio");
        }

        if (dishes == null || dishes.isEmpty()) {
            throw new IllegalArgumentException("El pedido debe contener al menos un plato");
        }

        if (dishes.size() > 20) {
            throw new IllegalArgumentException("El pedido no puede contener más de 20 platos diferentes");
        }

        dishes.forEach(OrderDish::validate);
    }

    public boolean isActive() {
        return status != OrderStatus.ENTREGADO && status != OrderStatus.CANCELADO;
    }

    public boolean canBeCancelled() {
        return status == OrderStatus.PENDIENTE;
    }

    public boolean canBeAssigned() {
        return status == OrderStatus.PENDIENTE && chefId == null;
    }

    public boolean canBeMarkedAsReady() {
        return status == OrderStatus.EN_PREPARACION && chefId != null;
    }

    public boolean canBeDelivered() {
        return status == OrderStatus.LISTO && securityPin != null;
    }
}