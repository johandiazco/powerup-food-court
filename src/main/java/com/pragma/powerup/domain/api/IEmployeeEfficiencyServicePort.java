package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.EmployeeEfficiency;
import java.util.List;

public interface IEmployeeEfficiencyServicePort {
    List<EmployeeEfficiency> getEmployeeEfficiencyRanking(Long restaurantId);
    EmployeeEfficiency getEmployeeEfficiency(Long employeeId, Long restaurantId);
}