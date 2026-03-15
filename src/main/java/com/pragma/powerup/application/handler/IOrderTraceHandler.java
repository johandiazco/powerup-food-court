package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.response.OrderTraceResponseDto;
import java.util.List;

public interface IOrderTraceHandler {
    List<OrderTraceResponseDto> getOrderTraceHistory(Long orderId);
}