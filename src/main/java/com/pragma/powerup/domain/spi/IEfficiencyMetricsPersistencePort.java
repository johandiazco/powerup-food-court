package com.pragma.powerup.domain.spi;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface IEfficiencyMetricsPersistencePort {
    List<Long> findEmployeesByRestaurant(Long restaurantId);
    Long countCompletedOrdersByEmployee(Long employeeId, Long restaurantId);
    Double calculateAveragePreparationTime(Long employeeId, Long restaurantId);
    List<Map<String, Object>> getEmployeeOrderDetails(Long employeeId, Long restaurantId);
}