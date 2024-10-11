package org.example.springwebpos.controller;

import lombok.RequiredArgsConstructor;
import org.example.springwebpos.dto.OrderDTO;
import org.example.springwebpos.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @PostMapping
    public ResponseEntity<OrderDTO> placeOrder(@RequestBody OrderDTO orderDTO) {
        logger.info("Received order placement request: {}", orderDTO);
        try {
            OrderDTO placedOrder = orderService.placeOrder(orderDTO);
            logger.info("Order placed successfully with ID: {}", placedOrder.getOrderId());
            return new ResponseEntity<>(placedOrder, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Failed to place order: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
