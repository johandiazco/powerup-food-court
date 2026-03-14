package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.UserRequestDto;
import com.pragma.powerup.application.dto.response.UserResponseDto;
import com.pragma.powerup.application.handler.IUserHandler;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints para gestión de usuarios")
public class UserRestController {

    private final IUserHandler userHandler;

    @Operation(
            summary = "Crear propietario (HU-1)",
            description = "Crea un nuevo usuario con rol PROPIETARIO. Solo usuarios ADMIN pueden crear propietarios."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Propietario creado exitosamente",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "Usuario ya existe")
    })
    @PostMapping("/propietario")
    public ResponseEntity<UserResponseDto> createPropietario(
            @Valid @RequestBody UserRequestDto requestDto) {
        UserResponseDto response = userHandler.createPropietario(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Crear empleado (HU-6)",
            description = "Crea un nuevo usuario con rol EMPLEADO. Solo usuarios PROPIETARIO pueden crear empleados."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Empleado creado exitosamente",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "Usuario ya existe")
    })
    @PostMapping("/empleado")
    public ResponseEntity<UserResponseDto> createEmpleado(
            @Valid @RequestBody UserRequestDto requestDto) {
        UserResponseDto response = userHandler.createEmpleado(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Crear cliente (HU-8)",
            description = "Crea un nuevo usuario con rol CLIENTE. Endpoint público para auto-registro."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Cliente creado exitosamente",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "Usuario ya existe")
    })
    @PostMapping("/cliente")
    public ResponseEntity<UserResponseDto> createCliente(
            @Valid @RequestBody UserRequestDto requestDto) {
        UserResponseDto response = userHandler.createCliente(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Obtener usuario por ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario encontrado",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        UserResponseDto response = userHandler.getUserById(id);
        return ResponseEntity.ok(response);
    }
}