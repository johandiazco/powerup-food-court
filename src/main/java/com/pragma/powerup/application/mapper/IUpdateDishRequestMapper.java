package com.pragma.powerup.application.mapper;

import com.pragma.powerup.application.dto.request.UpdateDishRequestDto;
import com.pragma.powerup.domain.model.Dish;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface IUpdateDishRequestMapper {
    Dish toDish(UpdateDishRequestDto updateDto);
}