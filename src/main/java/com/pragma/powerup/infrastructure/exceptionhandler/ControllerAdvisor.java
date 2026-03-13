package com.pragma.powerup.infrastructure.exceptionhandler;

import com.pragma.powerup.domain.exception.CategoryNotFoundException;
import com.pragma.powerup.domain.exception.DishNotFoundException;
import com.pragma.powerup.domain.exception.RestaurantAlreadyExistsException;
import com.pragma.powerup.domain.exception.RestaurantNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.time.LocalDateTime;
import java.util.Map;

@ControllerAdvice
public class ControllerAdvisor {

    private static final String MESSAGE = "message";
    private static final String TIMESTAMP = "timestamp";

    //RESTAURANT EXCEPTIONS HU-1
    @ExceptionHandler(RestaurantNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleRestaurantNotFoundException(
            RestaurantNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        MESSAGE, ex.getMessage(),
                        TIMESTAMP, LocalDateTime.now().toString()
                ));
    }

    @ExceptionHandler(RestaurantAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleRestaurantAlreadyExistsException(
            RestaurantAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                        MESSAGE, ex.getMessage(),
                        TIMESTAMP, LocalDateTime.now().toString()
                ));
    }

    //DISH EXCEPTIONS HU-2
    @ExceptionHandler(DishNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleDishNotFoundException(
            DishNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        MESSAGE, ex.getMessage(),
                        TIMESTAMP, LocalDateTime.now().toString()
                ));
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCategoryNotFoundException(
            CategoryNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        MESSAGE, ex.getMessage(),
                        TIMESTAMP, LocalDateTime.now().toString()
                ));
    }

    //GENERIC EXCEPTIONS
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        MESSAGE, ex.getMessage(),
                        TIMESTAMP, LocalDateTime.now().toString()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        MESSAGE, "Error interno del servidor: " + ex.getMessage(),
                        TIMESTAMP, LocalDateTime.now().toString()
                ));
    }
}