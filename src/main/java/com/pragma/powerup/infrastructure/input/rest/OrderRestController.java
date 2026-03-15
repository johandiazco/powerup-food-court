package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.OrderRequestDto;
import com.pragma.powerup.application.dto.response.OrderResponseDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    private final IOrderHandler orderHandler;
    private final JwtService jwtService;

    @Operation(summary = "Crear pedido",
            description = "HU-11: Permite a un cliente crear un nuevo pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente",
                    content = @Content(schema = @Schema(implementation = OrderResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "El cliente ya tiene un pedido activo en este restaurante")
    })
    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<OrderResponseDto> createOrder(
            @Valid @RequestBody OrderRequestDto orderRequestDto,
            @RequestHeader("Authorization") String token) {

        Long clientId = jwtService.getUserIdFromToken(token.replace("Bearer ", ""));
        OrderResponseDto response = orderHandler.createOrder(orderRequestDto, clientId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Obtener pedido por ID",
            description = "Obtiene los detalles de un pedido específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido encontrado",
                    content = @Content(schema = @Schema(implementation = OrderResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long id) {
        OrderResponseDto response = orderHandler.getOrderById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar pedidos por restaurante y estado",
            description = "HU-12: Permite a un empleado listar pedidos filtrados por restaurante y estado con paginación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pedidos obtenida exitosamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos")
    })
    @GetMapping("/restaurant/{restaurantId}")
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<Page<OrderResponseDto>> getOrdersByRestaurantAndStatus(
            @Parameter(description = "ID del restaurante") @PathVariable Long restaurantId,
            @Parameter(description = "Estado del pedido (PENDIENTE, EN_PREPARACION, LISTO, ENTREGADO, CANCELADO)")
            @RequestParam(defaultValue = "PENDIENTE") String status,
            @Parameter(description = "Número de página (inicia en 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<OrderResponseDto> response = orderHandler.getOrdersByRestaurantAndStatus(
                restaurantId, status, pageable);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Asignar pedido a empleado",
            description = "HU-13: Permite a un empleado tomar un pedido pendiente y asignárselo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido asignado exitosamente"),
            @ApiResponse(responseCode = "400", description = "El pedido no puede ser asignado"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<Void> assignOrderToChef(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        Long chefId = jwtService.getUserIdFromToken(token.replace("Bearer ", ""));
        orderHandler.assignOrderToChef(id, chefId);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Marcar pedido como listo",
            description = "HU-14: Permite al empleado asignado marcar un pedido como listo y generar PIN de seguridad")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido marcado como listo exitosamente"),
            @ApiResponse(responseCode = "400", description = "El pedido no puede ser marcado como listo"),
            @ApiResponse(responseCode = "403", description = "Solo el empleado asignado puede marcar el pedido como listo"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @PatchMapping("/{id}/ready")
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<Void> markOrderAsReady(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        Long chefId = jwtService.getUserIdFromToken(token.replace("Bearer ", ""));
        orderHandler.markOrderAsReady(id, chefId);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Entregar pedido",
            description = "HU-15: Permite al empleado entregar un pedido validando el PIN de seguridad")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido entregado exitosamente"),
            @ApiResponse(responseCode = "400", description = "PIN incorrecto o el pedido no puede ser entregado"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @PatchMapping("/{id}/deliver")
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<Void> deliverOrder(
            @PathVariable Long id,
            @Parameter(description = "PIN de seguridad de 6 dígitos") @RequestParam String pin) {

        orderHandler.deliverOrder(id, pin);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Cancelar pedido",
            description = "HU-16: Permite al cliente cancelar su pedido si está en estado PENDIENTE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido cancelado exitosamente"),
            @ApiResponse(responseCode = "400", description = "El pedido no puede ser cancelado"),
            @ApiResponse(responseCode = "403", description = "Solo el cliente que realizó el pedido puede cancelarlo"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        Long clientId = jwtService.getUserIdFromToken(token.replace("Bearer ", ""));
        orderHandler.cancelOrder(id, clientId);

        return ResponseEntity.ok().build();
    }
}