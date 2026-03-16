package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.RestaurantAlreadyExistsException;
import com.pragma.powerup.domain.exception.RestaurantNotFoundException;
import com.pragma.powerup.domain.model.Restaurant;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Restaurant Use Case Tests")
class RestaurantUseCaseTest {

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    private RestaurantUseCase restaurantUseCase;

    private Restaurant validRestaurant;

    @BeforeEach
    void setUp() {
        restaurantUseCase = new RestaurantUseCase(restaurantPersistencePort);

        validRestaurant = Restaurant.builder()
                .name("Restaurante El Buen Sabor")
                .nit("900123456-7")
                .address("Calle 123 #45-67, Medellín")
                .phone("+573001234567")
                .logoUrl("https://ejemplo.com/logo.png")
                .ownerId(10L)
                .build();
    }

    @Test
    @DisplayName("Should create restaurant successfully when all validations pass")
    void createRestaurant_WhenValidData_ShouldCreateSuccessfully() {

        when(restaurantPersistencePort.existsByNit("900123456-7")).thenReturn(false);
        when(restaurantPersistencePort.saveRestaurant(any(Restaurant.class))).thenReturn(validRestaurant);

        Restaurant result = restaurantUseCase.createRestaurant(validRestaurant);

        assertNotNull(result);
        assertEquals("Restaurante El Buen Sabor", result.getName());
        assertEquals("900123456-7", result.getNit());
        verify(restaurantPersistencePort, times(1)).saveRestaurant(any(Restaurant.class));
    }

    @Test
    @DisplayName("Should throw RestaurantAlreadyExistsException when NIT already exists")
    void createRestaurant_WhenNitAlreadyExists_ShouldThrowException() {

        when(restaurantPersistencePort.existsByNit("900123456-7")).thenReturn(true);

        assertThrows(RestaurantAlreadyExistsException.class, () -> restaurantUseCase.createRestaurant(validRestaurant));
        verify(restaurantPersistencePort, never()).saveRestaurant(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when name is empty")
    void createRestaurant_WhenNameIsEmpty_ShouldThrowException() {

        Restaurant invalidRestaurant = Restaurant.builder()
                .name("")
                .nit("900123456-7")
                .address("Calle 123")
                .phone("+573001234567")
                .logoUrl("https://ejemplo.com/logo.png")
                .ownerId(10L)
                .build();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> restaurantUseCase.createRestaurant(invalidRestaurant)
        );
        assertTrue(exception.getMessage().contains("nombre"));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when phone number is invalid")
    void createRestaurant_WhenInvalidPhoneNumber_ShouldThrowException() {
        // Arrange
        Restaurant invalidRestaurant = Restaurant.builder()
                .name("Restaurante Test")
                .nit("900123456-7")
                .address("Calle 123")
                .phone("123")
                .logoUrl("https://ejemplo.com/logo.png")
                .ownerId(10L)
                .build();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> restaurantUseCase.createRestaurant(invalidRestaurant)
        );
        assertTrue(exception.getMessage().contains("teléfono"));
    }

    @Test
    @DisplayName("Should get restaurant by ID when restaurant exists")
    void getRestaurantById_WhenRestaurantExists_ShouldReturnRestaurant() {

        Restaurant existingRestaurant = Restaurant.builder()
                .id(1L)
                .name("Restaurante El Buen Sabor")
                .nit("900123456-7")
                .address("Calle 123")
                .phone("+573001234567")
                .logoUrl("https://ejemplo.com/logo.png")
                .ownerId(10L)
                .build();

        when(restaurantPersistencePort.findRestaurantById(1L)).thenReturn(Optional.of(existingRestaurant));

        Restaurant result = restaurantUseCase.getRestaurantById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Restaurante El Buen Sabor", result.getName());
    }

    @Test
    @DisplayName("Should throw RestaurantNotFoundException when restaurant does not exist")
    void getRestaurantById_WhenRestaurantNotFound_ShouldThrowException() {

        when(restaurantPersistencePort.findRestaurantById(999L)).thenReturn(Optional.empty());

        assertThrows(RestaurantNotFoundException.class, () -> restaurantUseCase.getRestaurantById(999L));
    }

    @Test
    @DisplayName("Should verify restaurant exists by NIT")
    void existsByNit_ShouldReturnTrueWhenRestaurantExists() {

        when(restaurantPersistencePort.existsByNit("900123456-7")).thenReturn(true);

        boolean result = restaurantUseCase.existsByNit("900123456-7");

        assertTrue(result);
        verify(restaurantPersistencePort, times(1)).existsByNit("900123456-7");
    }
}