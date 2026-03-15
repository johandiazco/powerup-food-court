package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.response.OrderTraceResponseDto;
import com.pragma.powerup.application.handler.IOrderTraceHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders/trace")
@RequiredArgsConstructor
@Tag(name = "Trazabilidad", description = "Endpoints para consultar historial de cambios de estado de pedidos")
@SecurityRequirement(name = "jwt")
public class OrderTraceRestController {

    private final IOrderTraceHandler traceHandler;

    @Operation(
            summary = "Obtener historial de cambios de un pedido HU-17",
            description = "Retorna el historial completo de cambios de estado de un pedido específico. " +
                    "Cada registro incluye: estado anterior, nuevo estado, fecha, usuario que realizó el cambio. " +
                    "Solo accesible por EMPLEADO y ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Historial obtenido exitosamente",
                    content = @Content(schema = @Schema(implementation = OrderTraceResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pedido no encontrado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "No autorizado - Requiere rol EMPLEADO o ADMIN",
                    content = @Content
            )
    })
    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('EMPLEADO', 'ADMIN')")
    public ResponseEntity<List<OrderTraceResponseDto>> getOrderTraceHistory(
            @Parameter(description = "ID del pedido")
            @PathVariable Long orderId) {

        List<OrderTraceResponseDto> history = traceHandler.getOrderTraceHistory(orderId);

        return ResponseEntity.ok(history);
    }
}