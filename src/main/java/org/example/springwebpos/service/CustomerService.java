package org.example.springwebpos.service;

import org.example.springwebpos.customObj.CustomerResponse;
import org.example.springwebpos.dto.CustomerDTO;

import java.util.List;

public interface CustomerService {
    void saveCustomer(CustomerDTO customerDTO);

    void updateCustomer(CustomerDTO customerDTO);

    void deleteCustomer(String customerId);

    CustomerResponse getSelectedCustomer(String customerId);

    List<CustomerDTO> getAllCustomers();
}
