package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IOrderTraceServicePort;
import com.pragma.powerup.domain.model.OrderTrace;
import com.pragma.powerup.domain.spi.IOrderTracePersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class OrderTraceUseCase implements IOrderTraceServicePort {

    private final IOrderTracePersistencePort tracePort;

    @Override
    public void logStatusChange(
            Long orderId,
            String previousStatus,
            String newStatus,
            Long userId,
            String userEmail,
            String userRole) {

        OrderTrace trace = OrderTrace.builder()
                .orderId(orderId)
                .previousStatus(previousStatus)
                .newStatus(newStatus)
                .changeDate(LocalDateTime.now())
                .userId(userId)
                .userEmail(userEmail)
                .userRole(userRole)
                .build();

        trace.validate();

        OrderTrace savedTrace = tracePort.saveTrace(trace);

        log.info("✅ Trazabilidad registrada - Pedido: {}, Estado: {} → {}, Usuario: {}",
                orderId, previousStatus, newStatus, userEmail);
    }

    @Override
    public List<OrderTrace> getOrderHistory(Long orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("El ID del pedido es obligatorio");
        }

        return tracePort.findTracesByOrderId(orderId);
    }
}