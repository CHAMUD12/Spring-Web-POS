package org.example.springwebpos.controller;

import lombok.RequiredArgsConstructor;
import org.example.springwebpos.customObj.CustomerErrorResponse;
import org.example.springwebpos.customObj.CustomerResponse;
import org.example.springwebpos.dto.CustomerDTO;
import org.example.springwebpos.exception.CustomerNotFoundException;
import org.example.springwebpos.exception.DataPersistFailedException;
import org.example.springwebpos.service.CustomerService;
import org.example.springwebpos.util.AppUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomerErrorResponse> saveCustomer(
            @RequestPart(value = "name", required = false) String name,
            @RequestPart(value = "address", required = false) String address,
            @RequestPart(value = "mobile", required = false) String mobile,
            @RequestPart(value = "profilePic", required = false) MultipartFile profilePic) {
        logger.info("Request to save customer: Name={}, Address={}", name, address);

        // Validate name
        if (name == null) {
            logger.error("Name is missing");
            return new ResponseEntity<>(new CustomerErrorResponse("error", "Name is required"), HttpStatus.BAD_REQUEST);
        }
        if (!name.matches("^[a-zA-Z ]{3,20}$")) {
            logger.error("Invalid name: Name must only contain letters and be between 3 and 20 characters long");
            return new ResponseEntity<>(new CustomerErrorResponse("error", "Name must only contain letters and be between 3 and 20 characters long"), HttpStatus.BAD_REQUEST);
        }

        // Validate mobile
        if (mobile == null) {
            logger.error("Mobile number is missing");
            return new ResponseEntity<>(new CustomerErrorResponse("error", "Mobile number is required"), HttpStatus.BAD_REQUEST);
        }
        if (!mobile.matches("^0\\d{9}$")) {
            logger.error("Invalid mobile number: Mobile must be exactly 10 digits");
            return new ResponseEntity<>(new CustomerErrorResponse("error", "Mobile must be exactly 10 digits"), HttpStatus.BAD_REQUEST);
        }

        // Validate address
        if (address == null || address.isEmpty()) {
            logger.error("Address is missing");
            return new ResponseEntity<>(new CustomerErrorResponse("error", "Address is required"), HttpStatus.BAD_REQUEST);
        }
        if (!address.matches("^[a-zA-Z0-9, ]{1,100}$")) {
            logger.error("Invalid address: Address can only contain letters, digits, commas, spaces, and must be max 100 characters");
            return new ResponseEntity<>(new CustomerErrorResponse("error", "Address can only contain letters, digits, commas, spaces, and must be max 100 characters"), HttpStatus.BAD_REQUEST);
        }

        try {
            // Validate profile picture
            if (profilePic == null || profilePic.isEmpty()) {
                logger.error("Profile picture is missing");
                return new ResponseEntity<>(new CustomerErrorResponse("error", "Profile picture is required"), HttpStatus.BAD_REQUEST);
            }

            byte[] imageBytes = profilePic.getBytes();
            String base64ProfilePic = AppUtil.toBase64ProfilePic(imageBytes);

            // Build the customer object
            CustomerDTO buildCustomerDTO = new CustomerDTO();
            buildCustomerDTO.setName(name);
            buildCustomerDTO.setAddress(address);
            buildCustomerDTO.setMobile(mobile);
            buildCustomerDTO.setProfilePic(base64ProfilePic);

            // Send to the service layer
            customerService.saveCustomer(buildCustomerDTO);
            logger.info("Customer saved successfully: Name={}", name);
            return new ResponseEntity<>(new CustomerErrorResponse("success", "Customer saved successfully"), HttpStatus.CREATED);
        } catch (IOException e) {
            logger.error("Error processing profile picture: {}", e.getMessage(), e);
            return new ResponseEntity<>(new CustomerErrorResponse("error", "Error processing profile picture"), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (DataPersistFailedException e) {
            logger.error("Failed to save customer: {}", e.getMessage());
            return new ResponseEntity<>(new CustomerErrorResponse("error", "Failed to save customer"), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Unexpected error while saving customer: {}", e.getMessage(), e);
            return new ResponseEntity<>(new CustomerErrorResponse("error", "Unexpected error occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomerErrorResponse> updateCustomer(
            @PathVariable("id") String id,
            @RequestPart(value = "updateName", required = false) String updateName,
            @RequestPart(value = "updateAddress", required = false) String updateAddress,
            @RequestPart(value = "updateMobile", required = false) String updateMobile,
            @RequestPart(value = "updateProfilePic", required = false) MultipartFile updateProfilePic
    ) {
        logger.info("Request to update customer: ID={}", id);

        // Validate name
        if (updateName == null) {
            logger.error("Name is missing");
            return new ResponseEntity<>(new CustomerErrorResponse("error", "Name is required"), HttpStatus.BAD_REQUEST);
        }
        if (!updateName.matches("^[a-zA-Z ]{3,20}$")) {
            logger.error("Invalid name: Name must only contain letters and be between 3 and 20 characters long");
            return new ResponseEntity<>(new CustomerErrorResponse("error", "Name must only contain letters and be between 3 and 20 characters long"), HttpStatus.BAD_REQUEST);
        }

        // Validate mobile
        if (updateMobile == null) {
            logger.error("Mobile number is missing");
            return new ResponseEntity<>(new CustomerErrorResponse("error", "Mobile number is required"), HttpStatus.BAD_REQUEST);
        }
        if (!updateMobile.matches("^0\\d{9}$")) {
            logger.error("Invalid mobile number: Mobile must be exactly 10 digits");
            return new ResponseEntity<>(new CustomerErrorResponse("error", "Mobile must be exactly 10 digits"), HttpStatus.BAD_REQUEST);
        }

        // Validate address
        if (updateAddress == null || updateAddress.isEmpty()) {
            logger.error("Address is missing");
            return new ResponseEntity<>(new CustomerErrorResponse("error", "Address is required"), HttpStatus.BAD_REQUEST);
        }
        if (!updateAddress.matches("^[a-zA-Z0-9, ]{1,100}$")) {
            logger.error("Invalid address: Address can only contain letters, digits, commas, spaces, and must be max 100 characters");
            return new ResponseEntity<>(new CustomerErrorResponse("error", "Address can only contain letters, digits, commas, spaces, and must be max 100 characters"), HttpStatus.BAD_REQUEST);
        }

        try {
            // Validate profile picture
            if (updateProfilePic == null || updateProfilePic.isEmpty()) {
                logger.error("Profile picture is missing");
                return new ResponseEntity<>(new CustomerErrorResponse("error", "Profile picture is required"), HttpStatus.BAD_REQUEST);
            }

            byte[] imageBytes = updateProfilePic.getBytes();
            String updateBase64ProfilePic = AppUtil.toBase64ProfilePic(imageBytes);

            // Create the CustomerDTO object
            var updateCustomer = new CustomerDTO();
            updateCustomer.setId(id);
            updateCustomer.setName(updateName);
            updateCustomer.setAddress(updateAddress);
            updateCustomer.setMobile(updateMobile);
            updateCustomer.setProfilePic(updateBase64ProfilePic);

            // Call the service to update the customer
            customerService.updateCustomer(updateCustomer);
            logger.info("Customer updated successfully: ID={}", id);
            return new ResponseEntity<>(new CustomerErrorResponse("success", "Customer updated successfully"), HttpStatus.NO_CONTENT);
        } catch (CustomerNotFoundException e) {
            logger.warn("Customer not found for update: ID={}", id);
            return new ResponseEntity<>(new CustomerErrorResponse("error", "Customer not found"), HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            logger.error("Error processing profile picture: {}", e.getMessage(), e);
            return new ResponseEntity<>(new CustomerErrorResponse("error", "Error processing profile picture"), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Unexpected error while updating customer: ID={}, Error={}", id, e.getMessage());
            return new ResponseEntity<>(new CustomerErrorResponse("error", "Unexpected error occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable("id") String customerId) {
        logger.info("Request to delete customer: ID={}", customerId);
        try {
            customerService.deleteCustomer(customerId);
            logger.info("Customer deleted successfully: ID={}", customerId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (CustomerNotFoundException e) {
            logger.warn("Customer not found for deletion: ID={}", customerId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Unexpected error while deleting customer: ID={}, Error={}", customerId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CustomerResponse getSelectedCustomer(@PathVariable ("id") String id)  {
        logger.info("Request to get customer: ID={}", id);
        return customerService.getSelectedCustomer(id);
    }

    @GetMapping(value = "allcustomers", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CustomerDTO> getAllNotes(){
        logger.info("Request to get all customers");
        return customerService.getAllCustomers();
    }
}
