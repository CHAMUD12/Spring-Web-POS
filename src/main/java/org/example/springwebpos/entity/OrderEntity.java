package org.example.springwebpos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "orders")
@Entity
public class OrderEntity implements SuperEntity {
    @Id
    private String orderId;
    @ManyToOne
    @JoinColumn(name = "customerId", nullable = false)
    private CustomerEntity customer;
    private String orderDate;
    private double total;
    private double discount;
    private double subTotal;
    private double cash;
    private double balance;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetailEntity> orderDetails;

}
