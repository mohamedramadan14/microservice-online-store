package com.mo.orderservice.core.data.entity;

import com.mo.orderservice.command.models.OrderStatus;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id
    @Column(unique = true)
    private String orderId;
    private String productId;
    private String userId;
    private Integer qty;
    private String addressId;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
}
