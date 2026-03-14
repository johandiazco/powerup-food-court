package com.pragma.powerup.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.Period;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;
    private String nombre;
    private String apellido;
    private String documentoIdentidad;
    private String celular;
    private LocalDate fechaNacimiento;
    private String correo;
    private String clave;
    private Role rol;

    public void validate() {
        validateNombre();
        validateApellido();
        validateDocumentoIdentidad();
        validateCelular();
        validateFechaNacimiento();
        validateCorreo();
        validateClave();
        validateRol();
    }

    private void validateNombre() {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (nombre.length() > 100) {
            throw new IllegalArgumentException("El nombre no puede exceder 100 caracteres");
        }
    }

    private void validateApellido() {
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido es obligatorio");
        }
        if (apellido.length() > 100) {
            throw new IllegalArgumentException("El apellido no puede exceder 100 caracteres");
        }
    }

    private void validateDocumentoIdentidad() {
        if (documentoIdentidad == null || documentoIdentidad.trim().isEmpty()) {
            throw new IllegalArgumentException("El documento de identidad es obligatorio");
        }
        if (!documentoIdentidad.matches("^[0-9]+$")) {
            throw new IllegalArgumentException("El documento de identidad debe ser únicamente numérico");
        }
        if (documentoIdentidad.length() > 20) {
            throw new IllegalArgumentException("El documento de identidad no puede exceder 20 caracteres");
        }
    }

    private void validateCelular() {
        if (celular == null || celular.trim().isEmpty()) {
            throw new IllegalArgumentException("El celular es obligatorio");
        }
        if (!celular.matches("^\\+?[0-9]{10,13}$")) {
            throw new IllegalArgumentException(
                    "El celular debe contener máximo 13 caracteres y puede contener el símbolo +"
            );
        }
    }

    private void validateFechaNacimiento() {
        if (fechaNacimiento == null) {
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria");
        }

        LocalDate today = LocalDate.now();
        int edad = Period.between(fechaNacimiento, today).getYears();

        if (edad < 18) {
            throw new IllegalArgumentException("El usuario debe ser mayor de edad (18 años o más)");
        }

        if (fechaNacimiento.isAfter(today)) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura");
        }
    }

    private void validateCorreo() {
        if (correo == null || correo.trim().isEmpty()) {
            throw new IllegalArgumentException("El correo es obligatorio");
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!correo.matches(emailRegex)) {
            throw new IllegalArgumentException("El formato del correo electrónico no es válido");
        }

        if (correo.length() > 100) {
            throw new IllegalArgumentException("El correo no puede exceder 100 caracteres");
        }
    }

    private void validateClave() {
        if (clave == null || clave.trim().isEmpty()) {
            throw new IllegalArgumentException("La clave es obligatoria");
        }
        if (clave.length() < 6) {
            throw new IllegalArgumentException("La clave debe tener al menos 6 caracteres");
        }
    }

    private void validateRol() {
        if (rol == null) {
            throw new IllegalArgumentException("El rol es obligatorio");
        }
    }

    //Verificamos si el usuario es >18
    public boolean isMayorDeEdad() {
        if (fechaNacimiento == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        int edad = Period.between(fechaNacimiento, today).getYears();
        return edad >= 18;
    }
}