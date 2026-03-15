package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.OrderTrace;
import java.util.List;

public interface IOrderTracePersistencePort {
    OrderTrace saveTrace(OrderTrace trace);
    List<OrderTrace> findTracesByOrderId(Long orderId);
    List<OrderTrace> findAllTraces();
}