package org.example.springwebpos.controller;

import lombok.RequiredArgsConstructor;
import org.example.springwebpos.customObj.CustomerResponse;
import org.example.springwebpos.dto.CustomerDTO;
import org.example.springwebpos.exception.CustomerNotFoundException;
import org.example.springwebpos.exception.DataPersistFailedException;
import org.example.springwebpos.service.CustomerService;
import org.example.springwebpos.util.AppUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> saveCustomer(
            @RequestPart("name") String name,
            @RequestPart("address") String address,
            @RequestPart("mobile") String mobile,
            @RequestPart("profilePic") MultipartFile profilePic) {

        // Handle profile picture
        try {
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
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (DataPersistFailedException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateCustomer(
            @PathVariable("id") String id,
            @RequestPart("updateName") String updateName,
            @RequestPart("updateAddress") String updateAddress,
            @RequestPart("updateMobile") String updateMobile,
            @RequestPart("updateProfilePic") MultipartFile updateProfilePic
    ) {
        try {
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
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (CustomerNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable("id") String customerId) {
        try {
            customerService.deleteCustomer(customerId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (CustomerNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CustomerResponse getSelectedCustomer(@PathVariable ("id") String id)  {
        return customerService.getSelectedCustomer(id);
    }

    @GetMapping(value = "allcustomers", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CustomerDTO> getAllNotes(){
        return customerService.getAllCustomers();
    }
}
