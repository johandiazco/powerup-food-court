package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.spi.IEfficiencyMetricsPersistencePort;
import com.pragma.powerup.infrastructure.out.jpa.repository.IOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class EfficiencyMetricsJpaAdapter implements IEfficiencyMetricsPersistencePort {

    private final IOrderRepository orderRepository;

    @Override
    public List<Long> findEmployeesByRestaurant(Long restaurantId) {
        return orderRepository.findDistinctChefIdsByRestaurant(restaurantId);
    }

    @Override
    public Long countCompletedOrdersByEmployee(Long employeeId, Long restaurantId) {
        return orderRepository.countCompletedOrdersByEmployeeAndRestaurant(employeeId, restaurantId);
    }

    @Override
    public Double calculateAveragePreparationTime(Long employeeId, Long restaurantId) {
        //Obtenemos detalles de pedidos del empleado
        List<Map<String, Object>> orderDetails = getEmployeeOrderDetails(employeeId, restaurantId);

        if (orderDetails.isEmpty()) {
            return 0.0;
        }

        //Calculamos tiempos de preparación usando trazabilidad
        List<Long> preparationTimes = orderDetails.stream()
                .map(this::calculatePreparationTimeFromTraces)
                .filter(time -> time != null && time > 0)
                .collect(Collectors.toList());

        if (preparationTimes.isEmpty()) {
            return 0.0;
        }

        //Calculamos promedio en minutos
        double avgMillis = preparationTimes.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);

        return avgMillis / 60000.0; // Convertimos a minutos
    }

    @Override
    public List<Map<String, Object>> getEmployeeOrderDetails(Long employeeId, Long restaurantId) {
        List<Map<String, Object>> details = new ArrayList<>();
        return details;
    }

    private Long calculatePreparationTimeFromTraces(Map<String, Object> orderData) {
        try {
            LocalDateTime assignedDate = (LocalDateTime) orderData.get("assignedDate");
            LocalDateTime readyDate = (LocalDateTime) orderData.get("readyDate");

            if (assignedDate != null && readyDate != null) {
                Duration duration = Duration.between(assignedDate, readyDate);
                return duration.toMillis();
            }
        } catch (Exception e) {
            log.error("Error calculando tiempo de preparación: {}", e.getMessage());
        }

        return null;
    }
}