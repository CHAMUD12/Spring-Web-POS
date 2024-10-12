package org.example.springwebpos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderDTO implements SuperDTO {
    private String orderId;
    private String orderDate;
    private String customerId;
    private double total;
    private double discount;
    private double subTotal;
    private double cash;
    private double balance;
    private List<OrderDetailDTO> orderDetails = new ArrayList<>();
}
