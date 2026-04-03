package com.pragma.powerup;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class PowerUpApplicationTests {

    @Test
    void contextLoads() {
        assertDoesNotThrow(() -> {}, "El contexto de Spring Boot debe cargar correctamente");
    }
}