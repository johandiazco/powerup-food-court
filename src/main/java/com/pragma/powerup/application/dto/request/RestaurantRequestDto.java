package com.pragma.powerup.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantRequestDto {

    @Schema(description = "Nombre del restaurante", example = "El Buen Sabor")
    @NotBlank(message = "El nombre del restaurante es obligatorio")
    @Pattern(regexp = "^(?=.*[a-zA-Z]).+$", message = "El nombre debe contener al menos una letra")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    private String name;

    @Schema(description = "NIT del restaurante", example = "900123456-7")
    @NotBlank(message = "El NIT es obligatorio")
    @Pattern(regexp = "^[0-9]+(-[0-9]+)?$", message = "El NIT debe ser numérico")
    @Size(max = 20, message = "El NIT no puede tener más de 20 caracteres")
    private String nit;

    @Schema(description = "Dirección del restaurante", example = "Calle 123 # 45-67")
    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 200, message = "La dirección no puede tener más de 200 caracteres")
    private String address;

    @Schema(description = "Teléfono del restaurante", example = "+573001234567")
    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^\\+?[0-9]{10,13}$", message = "El teléfono debe contener solo números y opcionalmente el símbolo '+', con máximo 13 caracteres")
    private String phone;

    @Schema(description = "URL del logo del restaurante", example = "https://example.com/logo.png")
    @NotBlank(message = "La URL del logo es obligatoria")
    @Size(max = 500, message = "La URL del logo no puede tener más de 500 caracteres")
    private String logoUrl;

    @Schema(description = "ID del propietario del restaurante", example = "1")
    @NotNull(message = "El ID del propietario es obligatorio")
    private Long ownerId;
}