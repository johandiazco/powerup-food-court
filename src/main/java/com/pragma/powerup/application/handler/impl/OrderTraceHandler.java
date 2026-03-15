package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.response.OrderTraceResponseDto;
import com.pragma.powerup.application.handler.IOrderTraceHandler;
import com.pragma.powerup.application.mapper.IOrderTraceResponseMapper;
import com.pragma.powerup.domain.api.IOrderTraceServicePort;
import com.pragma.powerup.domain.model.OrderTrace;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderTraceHandler implements IOrderTraceHandler {

    private final IOrderTraceServicePort traceServicePort;
    private final IOrderTraceResponseMapper responseMapper;

    @Override
    public List<OrderTraceResponseDto> getOrderTraceHistory(Long orderId) {
        List<OrderTrace> traces = traceServicePort.getOrderHistory(orderId);
        return responseMapper.toResponseDtoList(traces);
    }
}