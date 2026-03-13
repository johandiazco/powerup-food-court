package com.pragma.powerup.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DishRequestDto {

    private String name;
    private BigDecimal price;
    private String description;
    private String imageUrl;
    private Long categoryId;
    private Long restaurantId;
}