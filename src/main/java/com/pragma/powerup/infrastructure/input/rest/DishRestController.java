package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.DishRequestDto;
import com.pragma.powerup.application.dto.request.UpdateDishRequestDto;
import com.pragma.powerup.application.dto.response.DishResponseDto;
import com.pragma.powerup.application.handler.IDishHandler;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(
            summary = "Crear nuevo plato",
            description = "Crea un nuevo plato en el menú de un restaurante"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Plato creado exitosamente",
                    content = @Content(schema = @Schema(implementation = DishResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Restaurante o categoría no encontrado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<DishResponseDto> createDish(@RequestBody DishRequestDto requestDto) {
        DishResponseDto response = dishHandler.createDish(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Obtener plato por ID",
            description = "Retorna los datos de un plato específico"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Plato encontrado",
                    content = @Content(schema = @Schema(implementation = DishResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Plato no encontrado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<DishResponseDto> getDishById(@PathVariable Long id) {
        DishResponseDto response = dishHandler.getDishById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Listar platos por restaurante",
            description = "Retorna todos los platos de un restaurante específico"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de platos obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = DishResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Restaurante no encontrado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content
            )
    })
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<DishResponseDto>> getDishesByRestaurant(@PathVariable Long restaurantId) {
        List<DishResponseDto> response = dishHandler.getDishesByRestaurant(restaurantId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Actualizar plato HU-3",
            description = "Actualiza los datos de un plato existente. Solo se pueden modificar: precio, descripción, nombre, imagen y categoría"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Plato actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = DishResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Plato o categoría no encontrado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<DishResponseDto> updateDish(
            @PathVariable Long id,
            @RequestBody UpdateDishRequestDto updateDto) {
        DishResponseDto response = dishHandler.updateDish(id, updateDto);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Cambiar estado del plato HU-4",
            description = "Activa o desactiva un plato del menú"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Estado actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = DishResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Plato no encontrado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content
            )
    })
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
}