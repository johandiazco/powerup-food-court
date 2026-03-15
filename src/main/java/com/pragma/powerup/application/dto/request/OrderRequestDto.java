package com.pragma.powerup.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para crear un pedido")
public class OrderRequestDto {

    @Schema(description = "ID del restaurante", example = "1")
    @NotNull(message = "El ID del restaurante es obligatorio")
    private Long restaurantId;

    @Schema(description = "Lista de platos del pedido")
    @NotNull(message = "La lista de platos es obligatoria")
    @NotEmpty(message = "El pedido debe contener al menos un plato")
    @Size(max = 20, message = "El pedido no puede contener más de 20 platos diferentes")
    @Valid
    private List<OrderDishRequestDto> dishes;
}