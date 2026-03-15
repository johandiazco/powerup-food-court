package com.pragma.powerup.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDish {

    private Long id;
    private Long dishId;
    private Integer quantity;

    public void validate() {
        if (dishId == null) {
            throw new IllegalArgumentException("El ID del plato es obligatorio");
        }

        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
    }
}