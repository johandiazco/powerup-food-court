package com.pragma.powerup.infrastructure.exceptionhandler;

import com.pragma.powerup.domain.exception.RestaurantAlreadyExistsException;
import com.pragma.powerup.domain.exception.RestaurantNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        Map<String, String> fieldErrors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        errors.put("errors", fieldErrors);
        errors.put(TIMESTAMP, LocalDateTime.now());

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    //RESTAURANT EXCEPTIONS (HU-1)
    @ExceptionHandler(RestaurantAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleRestaurantAlreadyExists(
            RestaurantAlreadyExistsException ex) {
        Map<String, String> error = new HashMap<>();
        error.put(MESSAGE, ex.getMessage());
        error.put(TIMESTAMP, LocalDateTime.now().toString());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RestaurantNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleRestaurantNotFound(
            RestaurantNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put(MESSAGE, ex.getMessage());
        error.put(TIMESTAMP, LocalDateTime.now().toString());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put(MESSAGE, "Error interno del servidor: " + ex.getMessage());
        error.put(TIMESTAMP, LocalDateTime.now().toString());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}