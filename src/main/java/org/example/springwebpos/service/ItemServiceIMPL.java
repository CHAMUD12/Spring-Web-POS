package org.example.springwebpos.service;

import lombok.RequiredArgsConstructor;
import org.example.springwebpos.customObj.ItemResponse;
import org.example.springwebpos.dao.ItemDAO;
import org.example.springwebpos.dto.ItemDTO;
import org.example.springwebpos.entity.ItemEntity;
import org.example.springwebpos.exception.DataPersistFailedException;
import org.example.springwebpos.exception.ItemNotFound;
import org.example.springwebpos.util.AppUtil;
import org.example.springwebpos.util.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemServiceIMPL implements ItemService {
    @Autowired
    private final ItemDAO itemDAO;

    @Autowired
    private final Mapping mapping;

    @Override
    public void saveItem(ItemDTO itemDTO) {
        itemDTO.setCode(AppUtil.createItemId()); // Assuming you have a method for generating customer IDs
        ItemEntity savedItem = itemDAO.save(mapping.convertToItemEntity(itemDTO));
        if (savedItem == null) {
            throw new DataPersistFailedException("Cannot save data");
        }
    }

    @Override
    public void updateItem(String itemCode, ItemDTO incomeItemDTO) {
        Optional<ItemEntity> tmpItemEntity= itemDAO.findById(itemCode);
        if(!tmpItemEntity.isPresent()){
            throw new ItemNotFound("Item not found");
        }else {
            tmpItemEntity.get().setDescription(incomeItemDTO.getDescription());
            tmpItemEntity.get().setPrice(Double.parseDouble(incomeItemDTO.getPrice()));
            tmpItemEntity.get().setQty(Integer.parseInt(incomeItemDTO.getQty()));
        }
    }

    @Override
    public void deleteItem(String code) {

    }

    @Override
    public ItemResponse getSelectedItem(String code) {
        return null;
    }

    @Override
    public List<ItemDTO> getAllItems() {
        return List.of();
    }
}
