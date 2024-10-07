package org.example.springwebpos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.springwebpos.customObj.CustomerResponse;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomerDTO implements SuperDTO, CustomerResponse {
    private String id;
    private String name;
    private String address;
    private String mobile;
    private String profilePic;
    private List<OrderDTO> orders = new ArrayList<>();
}
