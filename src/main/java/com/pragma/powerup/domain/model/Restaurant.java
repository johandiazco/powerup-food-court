package com.pragma.powerup.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {

    private Long id;
    private String name;
    private String nit;
    private String address;
    private String phone;
    private String urlLogo;
    private Long ownerId;

    //VALIDACIÓN EN EL DOMINIO
    public void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del restaurante es obligatorio");
        }

        if (nit == null || nit.length() < 5) {
            throw new IllegalArgumentException("El NIT debe tener al menos 5 caracteres");
        }

        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("La dirección es obligatoria");
        }

        if (phone == null || !phone.matches("^\\+?[0-9]{10,13}$")) {
            throw new IllegalArgumentException(
                    "El teléfono debe tener entre 10 y 13 dígitos (puede incluir +)"
            );
        }

        if (ownerId == null || ownerId <= 0) {
            throw new IllegalArgumentException("El ID del propietario es inválido");
        }
    }
}
