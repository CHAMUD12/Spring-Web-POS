package org.example.springwebpos.service;

import lombok.RequiredArgsConstructor;
import org.example.springwebpos.dao.CustomerDAO;
import org.example.springwebpos.dao.ItemDAO;
import org.example.springwebpos.dao.OrderDAO;
import org.example.springwebpos.dto.OrderDTO;
import org.example.springwebpos.entity.CustomerEntity;
import org.example.springwebpos.entity.ItemEntity;
import org.example.springwebpos.entity.OrderDetailEntity;
import org.example.springwebpos.entity.OrderEntity;
import org.example.springwebpos.util.AppUtil;
import org.example.springwebpos.util.Mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceIMPL implements OrderService {
    private final OrderDAO orderDAO;
    private final CustomerDAO customerDAO;
    private final ItemDAO itemDAO;
    private final Mapping mapping;
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceIMPL.class);

    @Override
    public OrderDTO placeOrder(OrderDTO orderDTO) {
        logger.info("Saving order for customer ID: {}", orderDTO.getCustomerId());

        // Generate Order ID
        if (orderDTO.getOrderId() == null || orderDTO.getOrderId().isEmpty()) {
            orderDTO.setOrderId(AppUtil.createOrderId());
            logger.debug("Generated new order ID: {}", orderDTO.getOrderId());
        }

        // Fetch and validate customer
        CustomerEntity customer = customerDAO.findById(orderDTO.getCustomerId())
                .orElseThrow(() -> {
                    logger.error("Customer ID {} not found", orderDTO.getCustomerId());
                    return new RuntimeException("Customer not found with ID: " + orderDTO.getCustomerId());
                });
        logger.debug("Customer name: {}", customer.getName());

        // Convert DTO to Entity and set customer
        OrderEntity orderEntity = mapping.convertToOrderEntity(orderDTO);
        orderEntity.setCustomer(customer);

        logger.debug("Processing order details");
        List<OrderDetailEntity> orderDetails = orderDTO.getOrderDetails().stream().map(orderDetailDTO -> {
            OrderDetailEntity orderDetail = new OrderDetailEntity();
            orderDetail.setOrder(orderEntity);

            // Fetch and validate item
            ItemEntity item = itemDAO.findById(orderDetailDTO.getItemCode())
                    .orElseThrow(() -> {
                        logger.error("Item ID {} not found", orderDetailDTO.getItemCode());
                        return new RuntimeException("Item not found with code: " + orderDetailDTO.getItemCode());
                    });
            logger.debug("Found item: {}", item.getDescription());

            // Check available quantity
            if (item.getQty() < orderDetailDTO.getQuantity()) {
                logger.error("Insufficient quantity for item: {} (requested: {}, available: {})",
                        item.getCode(), orderDetailDTO.getQuantity(), item.getQty());
                throw new RuntimeException("Insufficient quantity for item: " + item.getCode());
            }

            // Update Item quantity
            item.setQty(item.getQty() - orderDetailDTO.getQuantity());
            logger.info("Updated item quantity for item: {}. New quantity: {}", item.getCode(), item.getQty());
            itemDAO.save(item);

            // Set OrderDetail fields
            orderDetail.setItem(item);
            orderDetail.setQuantity(orderDetailDTO.getQuantity());
            orderDetail.setUnitPrice(orderDetailDTO.getUnitPrice());

            // Get OrderDetail totalAmount (Temporarily)
            double detailSubtotal = orderDetailDTO.getQuantity() * orderDetailDTO.getUnitPrice();
            orderDetail.setTotalPrice(detailSubtotal);

            return orderDetail;
        }).collect(Collectors.toList());

        orderEntity.setOrderDetails(orderDetails);

        // Calculate total
        double subTotal = orderDetails.stream()
                .mapToDouble(OrderDetailEntity::getTotalPrice)
                .sum();
        orderEntity.setTotal(subTotal);
        logger.debug("Calculated subtotal: {}", subTotal);

        // Calculate discount
        double discountPercent = orderDTO.getDiscount();
        double discountAmount = subTotal * discountPercent / 100;
        orderEntity.setDiscount(discountAmount);
        logger.debug("Calculated discount amount ({}%): {}", discountPercent, discountAmount);

        // Calculate subTotal after discount
        double total = subTotal - discountAmount;
        orderEntity.setSubTotal(total);
        logger.debug("Total after discount: {}", total);

        // Calculate balance
        double cash = orderDTO.getCash();
        double balance = cash - total;
        orderEntity.setBalance(balance);
        logger.debug("Calculated balance: {}", balance);

        // Each Item discount Set OrderDetail table
        if (subTotal > 0 && discountAmount > 0) {
            for (OrderDetailEntity detail : orderDetails) {
                double proportion = detail.getTotalPrice() / subTotal;
                double detailDiscount = discountAmount * proportion;
                double finalDetailTotal = detail.getTotalPrice() - detailDiscount;
                detail.setTotalPrice(finalDetailTotal);
                logger.debug("Applied discount to order detail ID {}: {} -> {}",
                        detail.getId(), detail.getTotalPrice() + detailDiscount, finalDetailTotal);
            }
        }

        // Save Order
        OrderEntity savedOrder = orderDAO.save(orderEntity);
        logger.info("Saved order: {}", savedOrder.getOrderId());
        return mapping.convertToOrderDTO(savedOrder);
    }
}
