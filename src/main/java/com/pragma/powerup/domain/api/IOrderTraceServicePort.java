package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.OrderTrace;
import java.util.List;

public interface IOrderTraceServicePort {

   void logStatusChange(
            Long orderId,
            String previousStatus,
            String newStatus,
            Long userId,
            String userEmail,
            String userRole
    );
    List<OrderTrace> getOrderHistory(Long orderId);
}