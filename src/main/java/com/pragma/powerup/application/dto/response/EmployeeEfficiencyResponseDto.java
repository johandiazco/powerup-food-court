package com.pragma.powerup.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Métricas de eficiencia de un empleado")
public class EmployeeEfficiencyResponseDto {

    @Schema(description = "ID del empleado", example = "7")
    private Long employeeId;

    @Schema(description = "Nombre completo del empleado", example = "Carlos Chef")
    private String employeeName;

    @Schema(description = "Email del empleado", example = "chef@plazoleta.com")
    private String employeeEmail;

    @Schema(description = "Total de pedidos completados (ENTREGADO)", example = "45")
    private Long completedOrders;

    @Schema(description = "Tiempo promedio de preparación en minutos", example = "18.5")
    private Double averagePreparationTimeMinutes;

    @Schema(description = "Índice de eficiencia (completados / tiempo promedio)", example = "2.43")
    private Double efficiencyIndex;

    @Schema(description = "Posición en el ranking (1 = más eficiente)", example = "1")
    private Integer rankingPosition;
}