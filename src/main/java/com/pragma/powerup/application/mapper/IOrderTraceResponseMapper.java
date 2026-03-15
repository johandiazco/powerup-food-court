package com.pragma.powerup.application.mapper;

import com.pragma.powerup.application.dto.response.OrderTraceResponseDto;
import com.pragma.powerup.domain.model.OrderTrace;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface IOrderTraceResponseMapper {
    OrderTraceResponseDto toResponseDto(OrderTrace trace);
    List<OrderTraceResponseDto> toResponseDtoList(List<OrderTrace> traces);
}