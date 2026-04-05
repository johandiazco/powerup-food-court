package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.model.OrderTrace;
import com.pragma.powerup.domain.spi.IOrderTracePersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderTrace Use Case Tests")
class OrderTraceUseCaseTest {

    @Mock
    private IOrderTracePersistencePort tracePort;

    private OrderTraceUseCase orderTraceUseCase;

    @BeforeEach
    void setUp() {
        orderTraceUseCase = new OrderTraceUseCase(tracePort);
    }

    @Test
    @DisplayName("HU-17: Should log status change successfully")
    void logStatusChange_ShouldSaveTrace() {
        OrderTrace savedTrace = OrderTrace.builder()
                .id("trace-1")
                .orderId(1L)
                .previousStatus("PENDIENTE")
                .newStatus("EN_PREPARACION")
                .userId(5L)
                .userEmail("chef@test.com")
                .userRole("EMPLEADO")
                .changeDate(LocalDateTime.now())
                .build();

        when(tracePort.saveTrace(any(OrderTrace.class))).thenReturn(savedTrace);

        orderTraceUseCase.logStatusChange(1L, "PENDIENTE", "EN_PREPARACION", 5L, "chef@test.com", "EMPLEADO");

        verify(tracePort).saveTrace(argThat(trace ->
                trace.getOrderId().equals(1L) &&
                        trace.getPreviousStatus().equals("PENDIENTE") &&
                        trace.getNewStatus().equals("EN_PREPARACION") &&
                        trace.getUserId().equals(5L)
        ));
    }

    @Test
    @DisplayName("HU-17: Should log initial status with null previous")
    void logStatusChange_WhenInitialStatus_ShouldAcceptNullPrevious() {
        when(tracePort.saveTrace(any(OrderTrace.class))).thenReturn(new OrderTrace());

        assertDoesNotThrow(() ->
                orderTraceUseCase.logStatusChange(1L, null, "PENDIENTE", 10L, "client@test.com", "CLIENTE")
        );

        verify(tracePort).saveTrace(any(OrderTrace.class));
    }

    @Test
    @DisplayName("HU-17: Should get order history")
    void getOrderHistory_ShouldReturnTraces() {
        List<OrderTrace> traces = Arrays.asList(
                OrderTrace.builder().id("t1").orderId(1L).newStatus("PENDIENTE").build(),
                OrderTrace.builder().id("t2").orderId(1L).newStatus("EN_PREPARACION").build(),
                OrderTrace.builder().id("t3").orderId(1L).newStatus("LISTO").build()
        );

        when(tracePort.findTracesByOrderId(1L)).thenReturn(traces);

        List<OrderTrace> result = orderTraceUseCase.getOrderHistory(1L);

        assertNotNull(result);
        assertEquals(3, result.size());
        verify(tracePort).findTracesByOrderId(1L);
    }

    @Test
    @DisplayName("HU-17: Should throw when orderId is null for history")
    void getOrderHistory_WhenOrderIdNull_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> orderTraceUseCase.getOrderHistory(null));
    }

    @Test
    @DisplayName("Should return empty list when no traces exist")
    void getOrderHistory_WhenNoTraces_ShouldReturnEmptyList() {
        when(tracePort.findTracesByOrderId(999L)).thenReturn(List.of());

        List<OrderTrace> result = orderTraceUseCase.getOrderHistory(999L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}