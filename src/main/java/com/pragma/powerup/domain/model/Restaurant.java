package com.pragma.powerup.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Restaurante {

    private Long id;
    private String nombre;
    private String nit;
    private String direccion;
    private String telefono;
    private String urlLogo;
    private Long propietarioId;

    //VALIDACIÓN EN EL DOMINIO
    public void validate() {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del restaurante es obligatorio");
        }

        if (nit == null || nit.length() < 5) {
            throw new IllegalArgumentException("El NIT debe tener al menos 5 caracteres");
        }

        if (direccion == null || direccion.trim().isEmpty()) {
            throw new IllegalArgumentException("La dirección es obligatoria");
        }

        if (telefono == null || !telefono.matches("^\\+?[0-9]{10,13}$")) {
            throw new IllegalArgumentException(
                    "El teléfono debe tener entre 10 y 13 dígitos (puede incluir +)"
            );
        }

        if (propietarioId == null || propietarioId <= 0) {
            throw new IllegalArgumentException("El ID del propietario es inválido");
        }
    }
}
