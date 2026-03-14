package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.DishRequestDto;
import com.pragma.powerup.application.dto.request.UpdateDishRequestDto;
import com.pragma.powerup.application.dto.response.DishResponseDto;
import com.pragma.powerup.application.dto.response.PageableResponseDto;
import com.pragma.powerup.application.handler.IDishHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/dishes")
@RequiredArgsConstructor
@Tag(name = "Dishes", description = "Endpoints para gestión de platos del menú")
public class DishRestController {

    private final IDishHandler dishHandler;

    @Operation(summary = "Crear nuevo plato (HU-3)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Plato creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Restaurante o categoría no encontrado")
    })
    @PostMapping
    public ResponseEntity<DishResponseDto> createDish(@RequestBody DishRequestDto requestDto) {
        DishResponseDto response = dishHandler.createDish(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Obtener plato por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plato encontrado"),
            @ApiResponse(responseCode = "404", description = "Plato no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DishResponseDto> getDishById(@PathVariable Long id) {
        DishResponseDto response = dishHandler.getDishById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar platos por restaurante")
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<DishResponseDto>> getDishesByRestaurant(@PathVariable Long restaurantId) {
        List<DishResponseDto> response = dishHandler.getDishesByRestaurant(restaurantId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Actualizar plato (HU-4)")
    @PutMapping("/{id}")
    public ResponseEntity<DishResponseDto> updateDish(
            @PathVariable Long id,
            @RequestBody UpdateDishRequestDto updateDto) {
        DishResponseDto response = dishHandler.updateDish(id, updateDto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cambiar estado del plato (HU-7)")
    @PatchMapping("/{id}/status")
    public ResponseEntity<DishResponseDto> toggleDishStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> statusUpdate) {
        Boolean active = statusUpdate.get("active");
        if (active == null) {
            throw new IllegalArgumentException("El campo 'active' es obligatorio");
        }
        DishResponseDto response = dishHandler.toggleDishStatus(id, active);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Listar platos activos con paginación (HU-10)",
            description = "Retorna los platos activos de un restaurante con soporte de paginación, filtrado por categoría y ordenamiento"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Página de platos obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = PageableResponseDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Restaurante no encontrado")
    })
    @GetMapping("/restaurant/{restaurantId}/active")
    public ResponseEntity<PageableResponseDto<DishResponseDto>> getActiveDishesPaginated(
            @Parameter(description = "ID del restaurante")
            @PathVariable Long restaurantId,

            @Parameter(description = "ID de categoría (opcional)")
            @RequestParam(required = false) Long categoryId,

            @Parameter(description = "Número de página (0-indexed)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Tamaño de página")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Campo para ordenar (name, price)")
            @RequestParam(defaultValue = "name") String sortBy,

            @Parameter(description = "Dirección de ordenamiento (asc, desc)")
            @RequestParam(defaultValue = "asc") String sortDirection) {

        PageableResponseDto<DishResponseDto> response = dishHandler.getActiveDishesPaginated(
                restaurantId, categoryId, page, size, sortBy, sortDirection);

        return ResponseEntity.ok(response);
    }
}