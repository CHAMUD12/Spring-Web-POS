package org.example.springwebpos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "items")
public class ItemEntity implements SuperEntity {
    @Id
    private String code;
    private String description;
    private double price;
    private int qty;
    @ManyToOne
    @JoinColumn(name = "orderId", nullable = false)
    private OrderEntity order;
}
