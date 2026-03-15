package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.response.EmployeeEfficiencyResponseDto;
import com.pragma.powerup.application.handler.IEmployeeEfficiencyHandler;
import com.pragma.powerup.application.mapper.IEmployeeEfficiencyResponseMapper;
import com.pragma.powerup.domain.api.IEmployeeEfficiencyServicePort;
import com.pragma.powerup.domain.model.EmployeeEfficiency;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeEfficiencyHandler implements IEmployeeEfficiencyHandler {

    private final IEmployeeEfficiencyServicePort efficiencyServicePort;
    private final IEmployeeEfficiencyResponseMapper responseMapper;

    @Override
    public List<EmployeeEfficiencyResponseDto> getEmployeeEfficiencyRanking(Long restaurantId) {
        List<EmployeeEfficiency> efficiencies =
                efficiencyServicePort.getEmployeeEfficiencyRanking(restaurantId);

        //Asignamos posiciones en el ranking
        AtomicInteger position = new AtomicInteger(1);

        return efficiencies.stream()
                .map(efficiency -> {
                    EmployeeEfficiencyResponseDto dto = responseMapper.toResponseDto(efficiency);
                    dto.setRankingPosition(position.getAndIncrement());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeEfficiencyResponseDto getEmployeeEfficiency(Long employeeId, Long restaurantId) {
        EmployeeEfficiency efficiency =
                efficiencyServicePort.getEmployeeEfficiency(employeeId, restaurantId);

        return responseMapper.toResponseDto(efficiency);
    }
}