package com.pragma.powerup.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    @Schema(description = "ID del usuario", example = "1")
    private Long id;

    @Schema(description = "Nombre del usuario", example = "Juan")
    private String nombre;

    @Schema(description = "Apellido del usuario", example = "Pérez")
    private String apellido;

    @Schema(description = "Documento de identidad", example = "1234567890")
    private String documentoIdentidad;

    @Schema(description = "Número de celular", example = "+573001234567")
    private String celular;

    @Schema(description = "Fecha de nacimiento", example = "1990-05-15")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;

    @Schema(description = "Correo electrónico", example = "juan.perez@example.com")
    private String correo;

    @Schema(description = "Rol del usuario", example = "PROPIETARIO")
    private String rol;
}