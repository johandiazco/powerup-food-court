package com.pragma.powerup.infrastructure.out.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "objectTable")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ObjectEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "object_id", nullable = false)
    private Long id;

    @Column(length = 50)
    private String name;
}
