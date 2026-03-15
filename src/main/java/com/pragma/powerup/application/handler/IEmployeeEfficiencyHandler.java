package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.response.EmployeeEfficiencyResponseDto;
import java.util.List;

public interface IEmployeeEfficiencyHandler {
    List<EmployeeEfficiencyResponseDto> getEmployeeEfficiencyRanking(Long restaurantId);
    EmployeeEfficiencyResponseDto getEmployeeEfficiency(Long employeeId, Long restaurantId);
}