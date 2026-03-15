package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IEmployeeEfficiencyServicePort;
import com.pragma.powerup.domain.exception.RestaurantNotFoundException;
import com.pragma.powerup.domain.model.EmployeeEfficiency;
import com.pragma.powerup.domain.model.User;
import com.pragma.powerup.domain.spi.IEfficiencyMetricsPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class EmployeeEfficiencyUseCase implements IEmployeeEfficiencyServicePort {

    private final IEfficiencyMetricsPersistencePort metricsPort;
    private final IUserPersistencePort userPort;
    private final IRestaurantPersistencePort restaurantPort;

    @Override
    public List<EmployeeEfficiency> getEmployeeEfficiencyRanking(Long restaurantId) {
        //Verificamos que el restaurante exista
        restaurantPort.findRestaurantById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

        //Obtenemos IDs de empleados que han trabajado en este restaurante
        List<Long> employeeIds = metricsPort.findEmployeesByRestaurant(restaurantId);

        if (employeeIds.isEmpty()) {
            log.info("No hay empleados con pedidos en el restaurante {}", restaurantId);
            return new ArrayList<>();
        }

        //Calculamos eficiencia de cada empleado
        List<EmployeeEfficiency> efficiencies = employeeIds.stream()
                .map(employeeId -> calculateEmployeeEfficiency(employeeId, restaurantId))
                .filter(efficiency -> efficiency.getCompletedOrders() > 0) // Solo empleados con pedidos
                .collect(Collectors.toList());

        //Ordenamos por índice de eficiencia
        efficiencies.sort(Comparator.comparing(EmployeeEfficiency::getEfficiencyIndex).reversed());

        log.info("Ranking de eficiencia calculado para restaurante {}. Total empleados: {}",
                restaurantId, efficiencies.size());

        return efficiencies;
    }

    @Override
    public EmployeeEfficiency getEmployeeEfficiency(Long employeeId, Long restaurantId) {
        //Se verificar que el restaurante exista
        restaurantPort.findRestaurantById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

        return calculateEmployeeEfficiency(employeeId, restaurantId);
    }

    private EmployeeEfficiency calculateEmployeeEfficiency(Long employeeId, Long restaurantId) {
        //Obtenemos información del empleado
        User employee = userPort.findUserById(employeeId).orElse(null);

        //Contamos pedidos completados
        Long completedOrders = metricsPort.countCompletedOrdersByEmployee(employeeId, restaurantId);

        //Calculamos tiempo promedio de preparación
        Double avgTime = metricsPort.calculateAveragePreparationTime(employeeId, restaurantId);

        //Creamos objeto de eficiencia
        EmployeeEfficiency efficiency = EmployeeEfficiency.builder()
                .employeeId(employeeId)
                .employeeName(employee != null
                        ? employee.getNombre() + " " + employee.getApellido()
                        : "Empleado Desconocido")
                .employeeEmail(employee != null ? employee.getCorreo() : "N/A")
                .completedOrders(completedOrders)
                .averagePreparationTimeMinutes(avgTime != null ? avgTime : 0.0)
                .build();

        //Calculamos índice de eficiencia
        efficiency.calculateEfficiency();

        return efficiency;
    }
}