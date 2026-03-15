package com.pragma.powerup.infrastructure.out.mongodb.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.LocalDateTime;

@Document(collection = "order_traces")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderTraceDocument {

    @Id
    private String id;

    @Field("order_id")
    private Long orderId;

    @Field("previous_status")
    private String previousStatus;

    @Field("new_status")
    private String newStatus;

    @Field("change_date")
    private LocalDateTime changeDate;

    @Field("user_id")
    private Long userId;

    @Field("user_email")
    private String userEmail;

    @Field("user_role")
    private String userRole;
}