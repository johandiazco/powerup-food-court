package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.OrderRequestDto;
import com.pragma.powerup.application.dto.response.OrderResponseDto;
import com.pragma.powerup.application.dto.response.PageableResponseDto;
import com.pragma.powerup.application.handler.IOrderHandler;
import com.pragma.powerup.infrastructure.security.jwt.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Endpoints para gestión de pedidos")
@SecurityRequirement(name = "jwt")
public class OrderRestController {

    private static final String BEARER_PREFIX = "Bearer ";

    private final IOrderHandler orderHandler;
    private final JwtService jwtService;

    @Operation(summary = "Crear pedido (HU-11)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente",
                    content = @Content(schema = @Schema(implementation = OrderResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "El cliente ya tiene un pedido activo")
    })
    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<OrderResponseDto> createOrder(
            @Valid @RequestBody OrderRequestDto orderRequestDto,
            @RequestHeader("Authorization") String token) {

        Long clientId = extractUserId(token);
        OrderResponseDto response = orderHandler.createOrder(orderRequestDto, clientId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Obtener pedido por ID")
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long id) {
        OrderResponseDto response = orderHandler.getOrderById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar pedidos por restaurante y estado (HU-12)")
    @GetMapping("/restaurant/{restaurantId}")
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<PageableResponseDto<OrderResponseDto>> getOrdersByRestaurantAndStatus(
            @Parameter(description = "ID del restaurante") @PathVariable Long restaurantId,
            @Parameter(description = "Estado del pedido")
            @RequestParam(defaultValue = "PENDIENTE") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageableResponseDto<OrderResponseDto> response =
                orderHandler.getOrdersByRestaurantAndStatus(restaurantId, status, page, size);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Asignar pedido a empleado (HU-13)")
    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<Void> assignOrderToChef(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        Long chefId = extractUserId(token);
        orderHandler.assignOrderToChef(id, chefId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Marcar pedido como listo (HU-14)")
    @PatchMapping("/{id}/ready")
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<Void> markOrderAsReady(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        Long chefId = extractUserId(token);
        orderHandler.markOrderAsReady(id, chefId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Entregar pedido (HU-15)")
    @PatchMapping("/{id}/deliver")
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<Void> deliverOrder(
            @PathVariable Long id,
            @Parameter(description = "PIN de seguridad") @RequestParam String pin) {

        orderHandler.deliverOrder(id, pin);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Cancelar pedido (HU-16)")
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        Long clientId = extractUserId(token);
        orderHandler.cancelOrder(id, clientId);
        return ResponseEntity.ok().build();
    }

    private Long extractUserId(String token) {
        return jwtService.getUserIdFromToken(token.replace(BEARER_PREFIX, ""));
    }
}