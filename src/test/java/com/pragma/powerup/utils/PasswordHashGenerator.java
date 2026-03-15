package com.pragma.powerup.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123";
        String hash = encoder.encode(password);

        System.out.println("=".repeat(60));
        System.out.println("Password: " + password);
        System.out.println("BCrypt Hash: " + hash);
        System.out.println("=".repeat(60));

        //Verificamos
        boolean matches = encoder.matches(password, hash);
        System.out.println("Verificación: " + matches);
    }
}