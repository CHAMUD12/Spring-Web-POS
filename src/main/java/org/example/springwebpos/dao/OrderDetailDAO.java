package org.example.springwebpos.dao;

import org.example.springwebpos.entity.OrderDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailDAO extends JpaRepository<OrderDetailEntity, Long> {
}
