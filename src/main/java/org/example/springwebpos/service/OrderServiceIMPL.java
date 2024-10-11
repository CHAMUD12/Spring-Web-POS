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
        logger.debug("Placing order for customer ID: {}", orderDTO.getCustomerId());

        // Generate a unique Order ID if not provided
        if (orderDTO.getOrderId() == null || orderDTO.getOrderId().isEmpty()) {
            orderDTO.setOrderId(AppUtil.createOrderId());
            logger.debug("Generated new Order ID: {}", orderDTO.getOrderId());
        }

        // Fetch and validate the CustomerEntity
        Optional<CustomerEntity> customerOpt = customerDAO.findById(orderDTO.getCustomerId());
        if (!customerOpt.isPresent()) {
            logger.error("Customer not found with ID: {}", orderDTO.getCustomerId());
            throw new RuntimeException("Customer not found with ID: " + orderDTO.getCustomerId());
        }
        CustomerEntity customer = customerOpt.get();

        // Convert OrderDTO to OrderEntity
        OrderEntity orderEntity = mapping.convertToOrderEntity(orderDTO);
        orderEntity.setCustomer(customer);

        // Process Order Details
        orderEntity.setOrderDetails(orderDTO.getOrderDetails().stream().map(orderDetailDTO -> {
            logger.debug("Processing order detail for item: {}", orderDetailDTO.getItemCode());
            OrderDetailEntity orderDetail = new OrderDetailEntity();
            orderDetail.setOrder(orderEntity);

            // Fetch and validate the ItemEntity
            Optional<ItemEntity> itemOpt = itemDAO.findById(orderDetailDTO.getItemCode());
            if (!itemOpt.isPresent()) {
                logger.error("Item not found with code: {}", orderDetailDTO.getItemCode());
                throw new RuntimeException("Item not found with code: " + orderDetailDTO.getItemCode());
            }
            ItemEntity item = itemOpt.get();

            // Check if sufficient quantity is available
            if (item.getQty() < orderDetailDTO.getQuantity()) {
                logger.warn("Insufficient quantity for item: {}", item.getCode());
                throw new RuntimeException("Insufficient quantity for item: " + item.getCode());
            }

            // Update item quantity
            item.setQty(item.getQty() - orderDetailDTO.getQuantity());
            itemDAO.save(item); // Persist the updated item quantity
            logger.debug("Updated item quantity for item: {}. Remaining stock: {}", item.getCode(), item.getQty());

            // Set Order Detail properties
            orderDetail.setItem(item);
            orderDetail.setQuantity(orderDetailDTO.getQuantity());
            orderDetail.setUnitPrice(orderDetailDTO.getUnitPrice());
            orderDetail.setTotalPrice(orderDetailDTO.getTotalPrice());

            return orderDetail;
        }).collect(Collectors.toList()));

        // Calculate Subtotal
        double subTotal = orderEntity.getOrderDetails().stream()
                .mapToDouble(OrderDetailEntity::getTotalPrice)
                .sum();
        orderEntity.setSubTotal(subTotal);
        logger.debug("Calculated subtotal: {}", subTotal);

        // Calculate Total after discount
        orderEntity.setTotal(subTotal - orderEntity.getDiscount());
        logger.debug("Final order total after discount: {}", orderEntity.getTotal());

        // Save the OrderEntity (OrderDetails are saved automatically due to CascadeType.ALL)
        OrderEntity savedOrder = orderDAO.save(orderEntity);
        logger.info("Order saved with ID: {}", savedOrder.getOrderId());

        // Convert the saved OrderEntity back to OrderDTO
        return mapping.convertToOrderDTO(savedOrder);
    }
}
