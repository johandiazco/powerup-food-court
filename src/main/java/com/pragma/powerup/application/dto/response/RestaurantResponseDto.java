package com.pragma.powerup.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantResponseDto {

    @Schema(description = "ID del restaurante", example = "1")
    private Long id;

    @Schema(description = "Nombre del restaurante", example = "El Buen Sabor")
    private String name;

    @Schema(description = "NIT del restaurante", example = "900123456-7")
    private String nit;

    @Schema(description = "Dirección del restaurante", example = "Calle 123 # 45-67")
    private String address;

    @Schema(description = "Teléfono del restaurante", example = "+573001234567")
    private String phone;

    @Schema(description = "URL del logo del restaurante", example = "https://example.com/logo.png")
    private String logoUrl;

    @Schema(description = "ID del propietario del restaurante", example = "1")
    private Long ownerId;
}