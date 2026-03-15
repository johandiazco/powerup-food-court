package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.response.EmployeeEfficiencyResponseDto;
import com.pragma.powerup.application.handler.IEmployeeEfficiencyHandler;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/efficiency")
@RequiredArgsConstructor
@Tag(name = "Eficiencia", description = "Endpoints para consultar métricas de eficiencia de empleados")
@SecurityRequirement(name = "jwt")
public class EmployeeEfficiencyRestController {

    private final IEmployeeEfficiencyHandler efficiencyHandler;

    @Operation(
            summary = "Ranking de empleados por eficiencia HU-18",
            description = "Retorna el ranking de empleados ordenados por índice de eficiencia. " +
                    "Eficiencia = (Pedidos completados) / (Tiempo promedio de preparación en minutos). " +
                    "Solo accesible por PROPIETARIO y ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Ranking obtenido exitosamente",
                    content = @Content(schema = @Schema(implementation = EmployeeEfficiencyResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Restaurante no encontrado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "No autorizado - Requiere rol PROPIETARIO o ADMIN",
                    content = @Content
            )
    })
    @GetMapping("/restaurant/{restaurantId}/ranking")
    @PreAuthorize("hasAnyRole('PROPIETARIO', 'ADMIN')")
    public ResponseEntity<List<EmployeeEfficiencyResponseDto>> getEmployeeEfficiencyRanking(
            @Parameter(description = "ID del restaurante")
            @PathVariable Long restaurantId) {

        List<EmployeeEfficiencyResponseDto> ranking =
                efficiencyHandler.getEmployeeEfficiencyRanking(restaurantId);

        return ResponseEntity.ok(ranking);
    }

    @Operation(
            summary = "Obtener eficiencia de un empleado específico HU-18",
            description = "Retorna las métricas de eficiencia de un empleado en un restaurante. " +
                    "Solo accesible por PROPIETARIO y ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Métricas obtenidas exitosamente",
                    content = @Content(schema = @Schema(implementation = EmployeeEfficiencyResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Empleado o restaurante no encontrado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "No autorizado",
                    content = @Content
            )
    })
    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('PROPIETARIO', 'ADMIN')")
    public ResponseEntity<EmployeeEfficiencyResponseDto> getEmployeeEfficiency(
            @Parameter(description = "ID del empleado")
            @PathVariable Long employeeId,

            @Parameter(description = "ID del restaurante")
            @RequestParam Long restaurantId) {

        EmployeeEfficiencyResponseDto efficiency =
                efficiencyHandler.getEmployeeEfficiency(employeeId, restaurantId);

        return ResponseEntity.ok(efficiency);
    }
}