package org.example.springwebpos.dto;

import java.util.List;

public class OrderDTO implements SuperDTO {
    private String orderId;
    private String orderDate;
    private String customerId;
    private double total;
    private double discount;
    private double subTotal;
    private double cash;
    private double balance;
    private List<ItemDTO> items;
}
