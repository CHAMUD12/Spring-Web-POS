package org.example.springwebpos.dao;

import org.example.springwebpos.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerDAO extends JpaRepository<CustomerEntity, String> {
    CustomerEntity getCustomerEntityById(String customerId);
}
