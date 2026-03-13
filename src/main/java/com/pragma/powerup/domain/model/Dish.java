package com.pragma.powerup.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Dish {

    private Long id;
    private String name;
    private BigDecimal price;
    private String description;
    private String imageUrl;
    private Long categoryId;
    private Long restaurantId;
    private Boolean active;

    public void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del plato no puede estar vacío");
        }

        if (name.length() > 100) {
            throw new IllegalArgumentException("El nombre del plato no puede exceder 100 caracteres");
        }

        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a cero");
        }

        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción del plato no puede estar vacía");
        }

        if (description.length() > 500) {
            throw new IllegalArgumentException("La descripción no puede exceder 500 caracteres");
        }

        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("La URL de la imagen no puede estar vacía");
        }

        if (!imageUrl.matches("^https?://.*")) {
            throw new IllegalArgumentException("La URL de la imagen debe comenzar con http:// o https://");
        }

        if (categoryId == null) {
            throw new IllegalArgumentException("La categoría es obligatoria");
        }

        if (restaurantId == null) {
            throw new IllegalArgumentException("El restaurante es obligatorio");
        }

        //Si active es null, cambia a true por defecto
        if (active == null) {
            active = true;
        }
    }
}
