package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IOrderTraceServicePort;
import com.pragma.powerup.domain.exception.ActiveOrderExistsException;
import com.pragma.powerup.domain.exception.DishNotFoundException;
import com.pragma.powerup.domain.exception.InvalidOrderOperationException;
import com.pragma.powerup.domain.exception.OrderNotFoundException;
import com.pragma.powerup.domain.exception.RestaurantNotFoundException;
import com.pragma.powerup.domain.model.Dish;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderDish;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.model.Restaurant;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Order Use Case Tests")
class OrderUseCaseTest {

    @Mock
    private IOrderPersistencePort orderPersistencePort;

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @Mock
    private IDishPersistencePort dishPersistencePort;

    @Mock
    private IOrderTraceServicePort traceService;

    @Mock
    private IUserPersistencePort userPersistencePort;

    private OrderUseCase orderUseCase;

    private Order validOrder;
    private Dish validDish;
    private Restaurant validRestaurant;

    @BeforeEach
    void setUp() {
        orderUseCase = new OrderUseCase(
                orderPersistencePort,
                restaurantPersistencePort,
                dishPersistencePort,
                traceService,
                userPersistencePort
        );

        //Configurar restaurante válido
        validRestaurant = Restaurant.builder()
                .id(1L)
                .name("Restaurante Test")
                .nit("123456789")
                .address("Calle Test 123")
                .phone("+573001234567")
                .logoUrl("https://example.com/logo.png")
                .ownerId(10L)
                .build();

        //Configurar plato válido
        validDish = new Dish();
        validDish.setId(1L);
        validDish.setName("Bandeja Paisa");
        validDish.setPrice(BigDecimal.valueOf(25000));
        validDish.setActive(true);
        validDish.setRestaurantId(1L);
        validDish.setCategoryId(1L);

        //Configurar orden válida
        validOrder = new Order();
        validOrder.setClientId(10L);
        validOrder.setRestaurantId(1L);

        List<OrderDish> dishes = new ArrayList<>();
        OrderDish orderDish = new OrderDish();
        orderDish.setDishId(1L);
        orderDish.setQuantity(2);
        dishes.add(orderDish);

        validOrder.setDishes(dishes);
    }

    @Test
    @DisplayName("Should create order successfully when all validations pass")
    void createOrder_WhenValidData_ShouldCreateSuccessfully() {

        when(restaurantPersistencePort.findRestaurantById(1L)).thenReturn(Optional.of(validRestaurant));
        when(orderPersistencePort.existsActiveOrderByClientIdAndRestaurantId(10L, 1L)).thenReturn(false);
        when(dishPersistencePort.findDishById(1L)).thenReturn(Optional.of(validDish));

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setClientId(10L);
        savedOrder.setRestaurantId(1L);
        savedOrder.setStatus(OrderStatus.PENDIENTE);
        savedOrder.setDate(LocalDateTime.now());

        when(orderPersistencePort.saveOrder(any(Order.class))).thenReturn(savedOrder);
        when(userPersistencePort.findUserById(anyLong())).thenReturn(Optional.empty());

        Order result = orderUseCase.createOrder(validOrder);

        assertNotNull(result);
        assertEquals(OrderStatus.PENDIENTE, result.getStatus());
        assertNotNull(result.getDate());
        assertNull(result.getChefId());

        verify(orderPersistencePort, times(1)).saveOrder(any(Order.class));
        verify(traceService, times(1)).logStatusChange(anyLong(), any(), any(), anyLong(), any(), any());
    }

    @Test
    @DisplayName("Should throw RestaurantNotFoundException when restaurant does not exist")
    void createOrder_WhenRestaurantNotFound_ShouldThrowException() {

        when(restaurantPersistencePort.findRestaurantById(1L)).thenReturn(Optional.empty());

        assertThrows(RestaurantNotFoundException.class, () -> orderUseCase.createOrder(validOrder));

        verify(orderPersistencePort, never()).saveOrder(any());
    }

    @Test
    @DisplayName("Should throw ActiveOrderExistsException when client has active order")
    void createOrder_WhenActiveOrderExists_ShouldThrowException() {

        when(restaurantPersistencePort.findRestaurantById(1L)).thenReturn(Optional.of(validRestaurant));
        when(orderPersistencePort.existsActiveOrderByClientIdAndRestaurantId(10L, 1L)).thenReturn(true);

        ActiveOrderExistsException exception = assertThrows(
                ActiveOrderExistsException.class,
                () -> orderUseCase.createOrder(validOrder)
        );

        assertTrue(exception.getMessage().contains("ya tiene un pedido activo"));
        verify(orderPersistencePort, never()).saveOrder(any());
    }

    @Test
    @DisplayName("Should throw DishNotFoundException when dish does not exist")
    void createOrder_WhenDishNotFound_ShouldThrowException() {

        when(restaurantPersistencePort.findRestaurantById(1L)).thenReturn(Optional.of(validRestaurant));
        when(orderPersistencePort.existsActiveOrderByClientIdAndRestaurantId(10L, 1L)).thenReturn(false);
        when(dishPersistencePort.findDishById(1L)).thenReturn(Optional.empty());

        assertThrows(DishNotFoundException.class, () -> orderUseCase.createOrder(validOrder));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when dish is inactive")
    void createOrder_WhenDishIsInactive_ShouldThrowException() {

        validDish.setActive(false);

        when(restaurantPersistencePort.findRestaurantById(1L)).thenReturn(Optional.of(validRestaurant));
        when(orderPersistencePort.existsActiveOrderByClientIdAndRestaurantId(10L, 1L)).thenReturn(false);
        when(dishPersistencePort.findDishById(1L)).thenReturn(Optional.of(validDish));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderUseCase.createOrder(validOrder)
        );

        assertTrue(exception.getMessage().contains("no está disponible"));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when dish belongs to different restaurant")
    void createOrder_WhenDishFromDifferentRestaurant_ShouldThrowException() {

        validDish.setRestaurantId(999L);

        when(restaurantPersistencePort.findRestaurantById(1L)).thenReturn(Optional.of(validRestaurant));
        when(orderPersistencePort.existsActiveOrderByClientIdAndRestaurantId(10L, 1L)).thenReturn(false);
        when(dishPersistencePort.findDishById(1L)).thenReturn(Optional.of(validDish));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderUseCase.createOrder(validOrder)
        );

        assertTrue(exception.getMessage().contains("no pertenece al restaurante"));
    }

    @Test
    @DisplayName("Should assign order to chef successfully")
    void assignOrderToChef_WhenOrderIsPending_ShouldAssignSuccessfully() {

        Order pendingOrder = new Order();
        pendingOrder.setId(1L);
        pendingOrder.setStatus(OrderStatus.PENDIENTE);
        pendingOrder.setClientId(10L);

        when(orderPersistencePort.findOrderById(1L)).thenReturn(Optional.of(pendingOrder));
        when(orderPersistencePort.saveOrder(any(Order.class))).thenAnswer(i -> i.getArgument(0));
        when(userPersistencePort.findUserById(anyLong())).thenReturn(Optional.empty());

        orderUseCase.assignOrderToChef(1L, 5L);

        verify(orderPersistencePort, times(1)).saveOrder(argThat(order ->
                order.getChefId().equals(5L) &&
                        order.getStatus() == OrderStatus.EN_PREPARACION
        ));
        verify(traceService, times(1)).logStatusChange(anyLong(), any(), any(), anyLong(), any(), any());
    }

    @Test
    @DisplayName("Should throw InvalidOrderOperationException when order is not pending")
    void assignOrderToChef_WhenOrderNotPending_ShouldThrowException() {

        Order completedOrder = new Order();
        completedOrder.setId(1L);
        completedOrder.setStatus(OrderStatus.ENTREGADO);

        when(orderPersistencePort.findOrderById(1L)).thenReturn(Optional.of(completedOrder));

        InvalidOrderOperationException exception = assertThrows(
                InvalidOrderOperationException.class,
                () -> orderUseCase.assignOrderToChef(1L, 5L)
        );

        assertTrue(exception.getMessage().contains("no puede ser asignado"));
    }

    @Test
    @DisplayName("Should mark order as ready and generate PIN")
    void markOrderAsReady_WhenOrderInPreparation_ShouldMarkAsReady() {

        Order preparingOrder = new Order();
        preparingOrder.setId(1L);
        preparingOrder.setStatus(OrderStatus.EN_PREPARACION);
        preparingOrder.setChefId(5L);
        preparingOrder.setClientId(10L);

        when(orderPersistencePort.findOrderById(1L)).thenReturn(Optional.of(preparingOrder));
        when(orderPersistencePort.saveOrder(any(Order.class))).thenAnswer(i -> i.getArgument(0));
        when(userPersistencePort.findUserById(anyLong())).thenReturn(Optional.empty());

        orderUseCase.markOrderAsReady(1L, 5L);

        verify(orderPersistencePort, times(1)).saveOrder(argThat(order ->
                order.getStatus() == OrderStatus.LISTO &&
                        order.getSecurityPin() != null &&
                        order.getSecurityPin().length() == 6
        ));
    }

    @Test
    @DisplayName("Should deliver order with correct PIN")
    void deliverOrder_WhenCorrectPIN_ShouldDeliverSuccessfully() {

        Order readyOrder = new Order();
        readyOrder.setId(1L);
        readyOrder.setStatus(OrderStatus.LISTO);
        readyOrder.setSecurityPin("123456");
        readyOrder.setChefId(5L);
        readyOrder.setClientId(10L);

        when(orderPersistencePort.findOrderById(1L)).thenReturn(Optional.of(readyOrder));
        when(orderPersistencePort.saveOrder(any(Order.class))).thenAnswer(i -> i.getArgument(0));
        when(userPersistencePort.findUserById(anyLong())).thenReturn(Optional.empty());

        orderUseCase.deliverOrder(1L, "123456");

        verify(orderPersistencePort, times(1)).saveOrder(argThat(order ->
                order.getStatus() == OrderStatus.ENTREGADO
        ));
    }

    @Test
    @DisplayName("Should cancel order when status is PENDIENTE")
    void cancelOrder_WhenOrderIsPending_ShouldCancelSuccessfully() {

        Order pendingOrder = new Order();
        pendingOrder.setId(1L);
        pendingOrder.setStatus(OrderStatus.PENDIENTE);
        pendingOrder.setClientId(10L);

        when(orderPersistencePort.findOrderById(1L)).thenReturn(Optional.of(pendingOrder));
        when(orderPersistencePort.saveOrder(any(Order.class))).thenAnswer(i -> i.getArgument(0));
        when(userPersistencePort.findUserById(anyLong())).thenReturn(Optional.empty());

        orderUseCase.cancelOrder(1L, 10L);

        verify(orderPersistencePort, times(1)).saveOrder(argThat(order ->
                order.getStatus() == OrderStatus.CANCELADO
        ));
    }

    @Test
    @DisplayName("Should get order by ID when order exists")
    void getOrderById_WhenOrderExists_ShouldReturnOrder() {

        Order existingOrder = new Order();
        existingOrder.setId(1L);
        existingOrder.setClientId(10L);

        when(orderPersistencePort.findOrderById(1L)).thenReturn(Optional.of(existingOrder));

        Order result = orderUseCase.getOrderById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(orderPersistencePort, times(1)).findOrderById(1L);
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when order does not exist")
    void getOrderById_WhenOrderNotFound_ShouldThrowException() {

        when(orderPersistencePort.findOrderById(999L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderUseCase.getOrderById(999L));
    }
}