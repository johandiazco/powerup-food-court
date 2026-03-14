package com.pragma.powerup.application.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {

    @Schema(description = "Nombre del usuario", example = "Juan")
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @Schema(description = "Apellido del usuario", example = "Pérez")
    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede exceder 100 caracteres")
    private String apellido;

    @Schema(description = "Documento de identidad", example = "1234567890")
    @NotBlank(message = "El documento de identidad es obligatorio")
    @Pattern(regexp = "^[0-9]+$", message = "El documento de identidad debe ser únicamente numérico")
    @Size(max = 20, message = "El documento no puede exceder 20 caracteres")
    private String documentoIdentidad;

    @Schema(description = "Número de celular", example = "+573001234567")
    @NotBlank(message = "El celular es obligatorio")
    @Pattern(
            regexp = "^\\+?[0-9]{10,13}$",
            message = "El celular debe contener entre 10 y 13 dígitos y puede incluir el símbolo +"
    )
    private String celular;

    @Schema(description = "Fecha de nacimiento", example = "1990-05-15")
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;

    @Schema(description = "Correo electrónico", example = "juan.perez@example.com")
    @NotBlank(message = "El correo es obligatorio")
    @Pattern(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "El formato del correo no es válido"
    )
    @Size(max = 100, message = "El correo no puede exceder 100 caracteres")
    private String correo;

    @Schema(description = "Contraseña (mínimo 6 caracteres)", example = "password123")
    @NotBlank(message = "La clave es obligatoria")
    @Size(min = 6, message = "La clave debe tener al menos 6 caracteres")
    private String clave;
}