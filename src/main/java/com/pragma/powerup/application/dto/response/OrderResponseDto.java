package com.pragma.powerup.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de respuesta para un pedido")
public class OrderResponseDto {

    @Schema(description = "ID del pedido", example = "1")
    private Long id;

    @Schema(description = "ID del cliente", example = "10")
    private Long clientId;

    @Schema(description = "Fecha del pedido", example = "2024-03-15T10:30:00")
    private LocalDateTime date;

    @Schema(description = "Estado del pedido", example = "PENDIENTE")
    private String status;

    @Schema(description = "ID del chef asignado", example = "7")
    private Long chefId;

    @Schema(description = "ID del restaurante", example = "3")
    private Long restaurantId;

    @Schema(description = "PIN de seguridad para entrega", example = "123456")
    private String securityPin;

    @Schema(description = "Lista de platos del pedido")
    private List<OrderDishResponseDto> dishes;
}