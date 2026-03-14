package com.pragma.powerup.infrastructure.exceptionhandler;

import com.pragma.powerup.domain.exception.CategoryNotFoundException;
import com.pragma.powerup.domain.exception.DishNotFoundException;
import com.pragma.powerup.domain.exception.RestaurantAlreadyExistsException;
import com.pragma.powerup.domain.exception.RestaurantNotFoundException;
import com.pragma.powerup.domain.exception.UserAlreadyExistsException;
import com.pragma.powerup.domain.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ControllerAdvisor {

    private static final String MESSAGE = "message";
    private static final String TIMESTAMP = "timestamp";

    //AUTHENTICATION EXCEPTIONS HU-5
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentialsException(
            BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        MESSAGE, ex.getMessage(),
                        TIMESTAMP, LocalDateTime.now().toString()
                ));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(
            AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        MESSAGE, "Error de autenticación: " + ex.getMessage(),
                        TIMESTAMP, LocalDateTime.now().toString()
                ));
    }

    //USER EXCEPTIONS HU-1, HU-6, HU-8
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFoundException(
            UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        MESSAGE, ex.getMessage(),
                        TIMESTAMP, LocalDateTime.now().toString()
                ));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleUserAlreadyExistsException(
            UserAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                        MESSAGE, ex.getMessage(),
                        TIMESTAMP, LocalDateTime.now().toString()
                ));
    }

    //RESTAURANT EXCEPTIONS HU-2
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

    //DISH EXCEPTIONS HU-3, HU-4
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

    //VALIDATION EXCEPTIONS
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "errors", errors,
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