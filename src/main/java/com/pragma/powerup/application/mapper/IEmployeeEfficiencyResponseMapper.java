package com.pragma.powerup.application.mapper;

import com.pragma.powerup.application.dto.response.EmployeeEfficiencyResponseDto;
import com.pragma.powerup.domain.model.EmployeeEfficiency;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface IEmployeeEfficiencyResponseMapper {
    EmployeeEfficiencyResponseDto toResponseDto(EmployeeEfficiency efficiency);
    List<EmployeeEfficiencyResponseDto> toResponseDtoList(List<EmployeeEfficiency> efficiencies);
}