package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.RestaurantAlreadyExistsException;
import com.pragma.powerup.domain.exception.RestaurantNotFoundException;
import com.pragma.powerup.domain.model.Restaurant;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Restaurant Use Case Tests")
class RestaurantUseCaseTest {

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;
    @InjectMocks
    private RestaurantUseCase restaurantUseCase;
    private Restaurant validRestaurant;

    @BeforeEach
    void setUp() {
        validRestaurant = Restaurant.builder()
                .name("La Casa del Sabor")
                .nit("900123456-7")
                .address("Calle 100 #15-20")
                .phone("+573001234567")
                .urlLogo("https://ejemplo.com/logo.png")
                .ownerId(1L)
                .build();
    }

    @Test
    @DisplayName("Should create restaurant successfully when data is valid and NIT is unique")
    void createRestaurant_WhenDataIsValidAndNitIsUnique_ShouldCreateSuccessfully() {
        Restaurant savedRestaurant = validRestaurant.toBuilder().id(1L).build();

        when(restaurantPersistencePort.existsByNit(anyString())).thenReturn(false);
        when(restaurantPersistencePort.saveRestaurant(any(Restaurant.class))).thenReturn(savedRestaurant);

        Restaurant result = restaurantUseCase.createRestaurant(validRestaurant);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("La Casa del Sabor", result.getName());
        assertEquals("900123456-7", result.getNit());

        verify(restaurantPersistencePort, times(1)).existsByNit("900123456-7");
        verify(restaurantPersistencePort, times(1)).saveRestaurant(validRestaurant);
    }

    @Test
    @DisplayName("Should throw RestaurantAlreadyExistsException when NIT already exists")
    void createRestaurant_WhenNitAlreadyExists_ShouldThrowException() {

        when(restaurantPersistencePort.existsByNit(anyString())).thenReturn(true);

        RestaurantAlreadyExistsException exception = assertThrows(
                RestaurantAlreadyExistsException.class,
                () -> restaurantUseCase.createRestaurant(validRestaurant)
        );

        assertEquals("Ya existe un restaurante con el NIT: 900123456-7", exception.getMessage());

        verify(restaurantPersistencePort, times(1)).existsByNit("900123456-7");
        verify(restaurantPersistencePort, never()).saveRestaurant(any(Restaurant.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when phone is invalid")
    void createRestaurant_WhenPhoneIsInvalid_ShouldThrowException() {

        Restaurant invalidRestaurant = validRestaurant.toBuilder()
                .phone("123")  // Teléfono inválido
                .build();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> restaurantUseCase.createRestaurant(invalidRestaurant)
        );

        assertEquals("El teléfono debe tener entre 10 y 13 dígitos (puede incluir +)",
                exception.getMessage());

        verify(restaurantPersistencePort, never()).existsByNit(anyString());
        verify(restaurantPersistencePort, never()).saveRestaurant(any(Restaurant.class));
    }

    @Test
    @DisplayName("Should get restaurant by ID successfully when restaurant exists")
    void getRestaurantById_WhenRestaurantExists_ShouldReturnRestaurant() {

        Long restaurantId = 1L;
        Restaurant existingRestaurant = validRestaurant.toBuilder().id(restaurantId).build();

        when(restaurantPersistencePort.findRestaurantById(restaurantId))
                .thenReturn(Optional.of(existingRestaurant));

        Restaurant result = restaurantUseCase.getRestaurantById(restaurantId);

        assertNotNull(result);
        assertEquals(restaurantId, result.getId());
        assertEquals("La Casa del Sabor", result.getName());

        verify(restaurantPersistencePort, times(1)).findRestaurantById(restaurantId);
    }

    @Test
    @DisplayName("Should throw RestaurantNotFoundException when restaurant does not exist")
    void getRestaurantById_WhenRestaurantDoesNotExist_ShouldThrowException() {

        Long restaurantId = 999L;

        when(restaurantPersistencePort.findRestaurantById(restaurantId))
                .thenReturn(Optional.empty());

        RestaurantNotFoundException exception = assertThrows(
                RestaurantNotFoundException.class,
                () -> restaurantUseCase.getRestaurantById(restaurantId)
        );

        assertEquals("No se encontró el restaurante con ID: 999", exception.getMessage());

        verify(restaurantPersistencePort, times(1)).findRestaurantById(restaurantId);
    }

    @Test
    @DisplayName("Should return true when NIT exists")
    void existsByNit_WhenNitExists_ShouldReturnTrue() {

        String nit = "900123456-7";
        when(restaurantPersistencePort.existsByNit(nit)).thenReturn(true);

        boolean result = restaurantUseCase.existsByNit(nit);

        assertTrue(result);
        verify(restaurantPersistencePort, times(1)).existsByNit(nit);
    }

    @Test
    @DisplayName("Should return false when NIT does not exist")
    void existsByNit_WhenNitDoesNotExist_ShouldReturnFalse() {

        String nit = "900123456-7";
        when(restaurantPersistencePort.existsByNit(nit)).thenReturn(false);

        boolean result = restaurantUseCase.existsByNit(nit);

        assertFalse(result);
        verify(restaurantPersistencePort, times(1)).existsByNit(nit);
    }
}
