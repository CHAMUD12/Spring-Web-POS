package org.example.springwebpos.util;

import org.example.springwebpos.dto.CustomerDTO;
import org.example.springwebpos.dto.ItemDTO;
import org.example.springwebpos.dto.OrderDTO;
import org.example.springwebpos.entity.CustomerEntity;
import org.example.springwebpos.entity.ItemEntity;
import org.example.springwebpos.entity.OrderEntity;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Mapping {
    @Autowired
    private ModelMapper modelMapper;

    public OrderDTO convertToDTO(OrderEntity order){
        return  modelMapper.map(order, OrderDTO.class);
    }
    public OrderEntity convertToEntity(OrderDTO dto){
        return modelMapper.map(dto, OrderEntity.class);
    }
    public List<OrderDTO> convertToDTO(List<OrderEntity> orders){
        return modelMapper.map(orders, new TypeToken<List<OrderDTO>>() {}.getType());
    }

    //User matters mapping
    public CustomerEntity convertToCustomerEntity(CustomerDTO customerDTO) {
        return modelMapper.map(customerDTO, CustomerEntity.class);
    }
    public CustomerDTO convertToCUstomerDTO(CustomerEntity customerEntity) {
        return modelMapper.map(customerEntity, CustomerDTO.class);
    }
    public List<CustomerDTO> convertCustomerListToDTO(List<CustomerEntity> customerEntities) {
        return modelMapper.map(customerEntities, new TypeToken<List<CustomerDTO>>() {}.getType());
    }

    //Item matters mapping
    public ItemEntity convertToItemEntity(ItemDTO itemDTO) {
        return modelMapper.map(itemDTO, ItemEntity.class);
    }
}
