package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.CategoryNotFoundException;
import com.pragma.powerup.domain.exception.DishNotFoundException;
import com.pragma.powerup.domain.exception.RestaurantNotFoundException;
import com.pragma.powerup.domain.model.Dish;
import com.pragma.powerup.domain.model.Restaurant;
import com.pragma.powerup.domain.spi.ICategoryPersistencePort;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("Dish Use Case Tests")
class DishUseCaseTest {

    @Mock
    private IDishPersistencePort dishPersistencePort;

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @Mock
    private ICategoryPersistencePort categoryPersistencePort;

    private DishUseCase dishUseCase;

    private Dish validDish;
    private Restaurant validRestaurant;

    @BeforeEach
    void setUp() {
        dishUseCase = new DishUseCase(dishPersistencePort, restaurantPersistencePort, categoryPersistencePort);

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

        //Configurar plato válido usando setters
        validDish = new Dish();
        validDish.setName("Bandeja Paisa");
        validDish.setPrice(BigDecimal.valueOf(25000));
        validDish.setDescription("Plato típico colombiano con carne, arroz, frijoles, huevo, chorizo, aguacate y arepa");
        validDish.setImageUrl("https://example.com/bandeja-paisa.jpg");
        validDish.setCategoryId(1L);
        validDish.setRestaurantId(1L);
        validDish.setActive(true);
    }

    @Test
    @DisplayName("Should create dish successfully when all validations pass")
    void createDish_WhenValidData_ShouldCreateSuccessfully() {

        when(restaurantPersistencePort.findRestaurantById(1L)).thenReturn(Optional.of(validRestaurant));
        when(categoryPersistencePort.existsById(1L)).thenReturn(true);
        when(dishPersistencePort.saveDish(any(Dish.class))).thenReturn(validDish);

        Dish result = dishUseCase.createDish(validDish);

        assertNotNull(result);
        assertEquals("Bandeja Paisa", result.getName());
        assertEquals(BigDecimal.valueOf(25000), result.getPrice());
        assertTrue(result.getActive());
        verify(dishPersistencePort, times(1)).saveDish(any(Dish.class));
    }

    @Test
    @DisplayName("Should throw RestaurantNotFoundException when restaurant does not exist")
    void createDish_WhenRestaurantNotFound_ShouldThrowException() {

        when(restaurantPersistencePort.findRestaurantById(1L)).thenReturn(Optional.empty());

        assertThrows(RestaurantNotFoundException.class, () -> dishUseCase.createDish(validDish));
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test
    @DisplayName("Should throw CategoryNotFoundException when category does not exist")
    void createDish_WhenCategoryNotFound_ShouldThrowException() {

        when(restaurantPersistencePort.findRestaurantById(1L)).thenReturn(Optional.of(validRestaurant));
        when(categoryPersistencePort.existsById(1L)).thenReturn(false);

        assertThrows(CategoryNotFoundException.class, () -> dishUseCase.createDish(validDish));
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when price is zero")
    void createDish_WhenPriceIsZero_ShouldThrowException() {

        validDish.setPrice(BigDecimal.ZERO);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> dishUseCase.createDish(validDish)
        );
        assertTrue(exception.getMessage().contains("precio"));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when description is empty")
    void createDish_WhenDescriptionIsEmpty_ShouldThrowException() {

        validDish.setDescription("");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> dishUseCase.createDish(validDish)
        );
        assertTrue(exception.getMessage().contains("descripción"));
    }

    @Test
    @DisplayName("Should get dish by ID successfully")
    void getDishById_WhenDishExists_ShouldReturnDish() {

        validDish.setId(1L);
        when(dishPersistencePort.findDishById(1L)).thenReturn(Optional.of(validDish));

        Dish result = dishUseCase.getDishById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Bandeja Paisa", result.getName());
    }

    @Test
    @DisplayName("Should throw DishNotFoundException when dish does not exist")
    void getDishById_WhenDishNotFound_ShouldThrowException() {

        when(dishPersistencePort.findDishById(999L)).thenReturn(Optional.empty());

        assertThrows(DishNotFoundException.class, () -> dishUseCase.getDishById(999L));
    }

    @Test
    @DisplayName("Should update dish price successfully")
    void updateDish_WhenValidPrice_ShouldUpdateSuccessfully() {

        validDish.setId(1L);
        when(dishPersistencePort.findDishById(1L)).thenReturn(Optional.of(validDish));
        when(dishPersistencePort.updateDish(any(Dish.class))).thenReturn(validDish);

        Dish updateData = new Dish();
        updateData.setPrice(BigDecimal.valueOf(30000));

        dishUseCase.updateDish(1L, updateData);

        verify(dishPersistencePort, times(1)).updateDish(argThat(dish ->
                dish.getPrice().equals(BigDecimal.valueOf(30000))
        ));
    }

    @Test
    @DisplayName("Should throw exception when updating with invalid price")
    void updateDish_WhenInvalidPrice_ShouldThrowException() {

        validDish.setId(1L);
        when(dishPersistencePort.findDishById(1L)).thenReturn(Optional.of(validDish));

        Dish updateData = new Dish();
        updateData.setPrice(BigDecimal.valueOf(-1000));

        assertThrows(IllegalArgumentException.class, () -> dishUseCase.updateDish(1L, updateData));
    }

    @Test
    @DisplayName("Should enable or disable dish successfully")
    void toggleDishStatus_ShouldToggleActiveStatus() {

        validDish.setId(1L);
        validDish.setActive(true);
        when(dishPersistencePort.findDishById(1L)).thenReturn(Optional.of(validDish));
        when(dishPersistencePort.updateDish(any(Dish.class))).thenReturn(validDish);

        dishUseCase.toggleDishStatus(1L, false);

        verify(dishPersistencePort, times(1)).updateDish(argThat(dish ->
                !dish.getActive()
        ));
    }

    @Test
    @DisplayName("Should throw exception when trying to change status of non-existent dish")
    void toggleDishStatus_WhenDishNotFound_ShouldThrowException() {

        when(dishPersistencePort.findDishById(999L)).thenReturn(Optional.empty());

        assertThrows(DishNotFoundException.class, () -> dishUseCase.toggleDishStatus(999L, false));
    }

    @Test
    @DisplayName("Should throw exception when description is too long")
    void createDish_WhenDescriptionTooLong_ShouldThrowException() {

        String longDescription = "a".repeat(501);
        validDish.setDescription(longDescription);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> dishUseCase.createDish(validDish)
        );
        assertTrue(exception.getMessage().contains("descripción") || exception.getMessage().contains("500"));
    }
}