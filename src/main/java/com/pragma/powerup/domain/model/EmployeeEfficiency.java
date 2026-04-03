package com.pragma.powerup.domain.model;

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
public class EmployeeEfficiency {

    private Long employeeId;
    private String employeeName;
    private String employeeEmail;
    private Long completedOrders;
    private Double averagePreparationTimeMinutes;

    public Double getEfficiencyIndex() {
        if (completedOrders == null || completedOrders == 0) {
            return 0.0;
        }
        if (averagePreparationTimeMinutes == null || averagePreparationTimeMinutes == 0) {
            return 0.0;
        }
        return completedOrders / averagePreparationTimeMinutes;
    }
}