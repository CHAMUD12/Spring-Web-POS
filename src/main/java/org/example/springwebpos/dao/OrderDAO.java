package org.example.springwebpos.dao;

import org.example.springwebpos.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDAO extends JpaRepository<OrderEntity, String> {
}
