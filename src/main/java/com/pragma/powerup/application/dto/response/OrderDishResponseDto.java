package com.pragma.powerup.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de respuesta para un plato dentro de un pedido")
public class OrderDishResponseDto {

    @Schema(description = "ID del plato en el pedido", example = "1")
    private Long id;

    @Schema(description = "ID del plato", example = "5")
    private Long dishId;

    @Schema(description = "Cantidad del plato", example = "2")
    private Integer quantity;
}