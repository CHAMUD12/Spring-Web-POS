package org.example.springwebpos.service;

import lombok.RequiredArgsConstructor;
import org.example.springwebpos.customObj.CustomerErrorResponse;
import org.example.springwebpos.customObj.CustomerResponse;
import org.example.springwebpos.dao.CustomerDAO;
import org.example.springwebpos.dto.CustomerDTO;
import org.example.springwebpos.entity.CustomerEntity;
import org.example.springwebpos.exception.CustomerNotFoundException;
import org.example.springwebpos.exception.DataPersistFailedException;
import org.example.springwebpos.util.AppUtil;
import org.example.springwebpos.util.Mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerServiceIMPL implements CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceIMPL.class);

    @Autowired
    private final CustomerDAO customerDAO;

    @Autowired
    private final Mapping mapping;

    @Override
    public void saveCustomer(CustomerDTO customerDTO) {
        logger.info("Saving customer: Name={}", customerDTO.getName());
        customerDTO.setId(AppUtil.createCustomerId()); // Assuming you have a method for generating customer IDs
        CustomerEntity savedCustomer = customerDAO.save(mapping.convertToCustomerEntity(customerDTO));
        if (savedCustomer == null) {
            logger.error("Failed to persist customer data: Name={}", customerDTO.getName());
            throw new DataPersistFailedException("Cannot save data");
        }
        logger.info("Customer saved successfully: Name={}", customerDTO.getName());
    }

    @Override
    public void updateCustomer(CustomerDTO customerDTO) {
        logger.info("Updating customer: ID={}", customerDTO.getId());
        Optional<CustomerEntity> tmpCustomer = customerDAO.findById(customerDTO.getId());
        if (!tmpCustomer.isPresent()) {
            logger.warn("Customer not found: ID={}", customerDTO.getId());
            throw new CustomerNotFoundException("Customer Not Found");
        } else {
            tmpCustomer.get().setName(customerDTO.getName());
            tmpCustomer.get().setAddress(customerDTO.getAddress());
            tmpCustomer.get().setMobile(customerDTO.getMobile());
            tmpCustomer.get().setProfilePic(customerDTO.getProfilePic());
            customerDAO.save(tmpCustomer.get());
            logger.info("Customer updated successfully: ID={}", customerDTO.getId());
        }
    }

    @Override
    public void deleteCustomer(String customerId) {
        logger.info("Deleting customer: ID={}", customerId);
        Optional<CustomerEntity> selectedCustomerId = customerDAO.findById(customerId);
        if (!selectedCustomerId.isPresent()) {
            logger.warn("Customer not found for deletion: ID={}", customerId);
            throw new CustomerNotFoundException("Customer not found");
        } else {
            customerDAO.deleteById(customerId);
            logger.info("Customer deleted successfully: ID={}", customerId);
        }
    }

    @Override
    public CustomerResponse getSelectedCustomer(String customerId) {
        logger.info("Fetching customer: ID={}", customerId);
        if(customerDAO.existsById(customerId)){
            return mapping.convertToCUstomerDTO(customerDAO.getReferenceById(customerId));
        }else {
            logger.warn("Customer not found: ID={}", customerId);
            return new CustomerErrorResponse("0","Customer not found");
        }
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        logger.info("Fetching all customers");
        return mapping.convertCustomerListToDTO(customerDAO.findAll());
    }
}
