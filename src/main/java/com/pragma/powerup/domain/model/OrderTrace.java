package com.pragma.powerup.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderTrace {

    private String id;
    private Long orderId;
    private String previousStatus;
    private String newStatus;
    private LocalDateTime changeDate;
    private Long userId;
    private String userEmail;
    private String userRole;

    public void validate() {
        if (orderId == null) {
            throw new IllegalArgumentException("El ID del pedido es obligatorio");
        }

        if (newStatus == null || newStatus.trim().isEmpty()) {
            throw new IllegalArgumentException("El nuevo estado es obligatorio");
        }

        if (userId == null) {
            throw new IllegalArgumentException("El ID del usuario es obligatorio");
        }

        if (changeDate == null) {
            changeDate = LocalDateTime.now();
        }
    }
}