package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.RestaurantRequestDto;
import com.pragma.powerup.application.dto.response.PageableResponseDto;
import com.pragma.powerup.application.dto.response.RestaurantListResponseDto;
import com.pragma.powerup.application.dto.response.RestaurantResponseDto;
import com.pragma.powerup.application.handler.IRestaurantHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
@Tag(name = "Restaurantes", description = "Endpoints para la gestión de restaurantes")
public class RestaurantRestController {

    private final IRestaurantHandler restaurantHandler;

    @Operation(
            summary = "Crear un nuevo restaurante (HU-2)",
            description = "Crea un nuevo restaurante en el sistema. Solo usuarios con rol ADMIN pueden crear restaurantes."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Restaurante creado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RestaurantResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos o restaurante con NIT ya existe",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "No autorizado - Se requiere rol ADMIN",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping
    public ResponseEntity<RestaurantResponseDto> createRestaurant(
            @Valid @RequestBody RestaurantRequestDto restaurantRequestDto) {
        RestaurantResponseDto response = restaurantHandler.createRestaurant(restaurantRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Obtener un restaurante por ID",
            description = "Retorna la información de un restaurante específico mediante su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Restaurante encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RestaurantResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Restaurante no encontrado",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponseDto> getRestaurantById(@PathVariable Long id) {
        RestaurantResponseDto response = restaurantHandler.getRestaurantById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Listar restaurantes con paginación (HU-9)",
            description = "Retorna una lista paginada de restaurantes ordenados alfabéticamente por nombre. " +
                    "Solo retorna nombre y logo de cada restaurante."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de restaurantes obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PageableResponseDto.class)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<PageableResponseDto<RestaurantListResponseDto>> getAllRestaurants(
            @Parameter(description = "Número de página (0-indexed)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Cantidad de elementos por página")
            @RequestParam(defaultValue = "10") int size) {

        PageableResponseDto<RestaurantListResponseDto> response =
                restaurantHandler.getAllRestaurants(page, size);

        return ResponseEntity.ok(response);
    }
}