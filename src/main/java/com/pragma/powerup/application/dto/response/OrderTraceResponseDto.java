package com.pragma.powerup.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Registro de cambio de estado de un pedido")
public class OrderTraceResponseDto {

    @Schema(description = "ID del registro de trazabilidad", example = "507f1f77bcf86cd799439011")
    private String id;

    @Schema(description = "ID del pedido", example = "15")
    private Long orderId;

    @Schema(description = "Estado anterior", example = "PENDIENTE")
    private String previousStatus;

    @Schema(description = "Nuevo estado", example = "EN_PREPARACION")
    private String newStatus;

    @Schema(description = "Fecha y hora del cambio", example = "2024-03-15T14:30:00")
    private LocalDateTime changeDate;

    @Schema(description = "ID del usuario que realizó el cambio", example = "7")
    private Long userId;

    @Schema(description = "Email del usuario", example = "chef@plazoleta.com")
    private String userEmail;

    @Schema(description = "Rol del usuario", example = "EMPLEADO")
    private String userRole;
}