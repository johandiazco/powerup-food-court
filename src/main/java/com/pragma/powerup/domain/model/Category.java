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
public class Category {

    private Long id;
    private String name;
    private String description;

    public void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio");
        }

        if (name.length() > 50) {
            throw new IllegalArgumentException("El nombre de la categoría no puede exceder 50 caracteres");
        }

        if (description != null && description.length() > 200) {
            throw new IllegalArgumentException("La descripción no puede exceder 200 caracteres");
        }
    }
}