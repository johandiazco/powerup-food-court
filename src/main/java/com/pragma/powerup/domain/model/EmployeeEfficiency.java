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
    private Double efficiencyIndex;

    public void calculateEfficiency() {
        if (completedOrders == null || completedOrders == 0) {
            this.efficiencyIndex = 0.0;
            return;
        }

        if (averagePreparationTimeMinutes == null || averagePreparationTimeMinutes == 0) {
            this.efficiencyIndex = 0.0;
            return;
        }

        //Implementamos indice de eficiencia= más pedidos + menos tiempo = mayor eficiencia
        this.efficiencyIndex = completedOrders / averagePreparationTimeMinutes;
    }
}