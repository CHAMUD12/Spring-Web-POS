package org.example.springwebpos.service;

import lombok.RequiredArgsConstructor;
import org.example.springwebpos.customObj.ItemErrorResponse;
import org.example.springwebpos.customObj.ItemResponse;
import org.example.springwebpos.dao.ItemDAO;
import org.example.springwebpos.dto.ItemDTO;
import org.example.springwebpos.entity.ItemEntity;
import org.example.springwebpos.exception.DataPersistFailedException;
import org.example.springwebpos.exception.ItemNotFound;
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
public class ItemServiceIMPL implements ItemService {

    private static final Logger logger = LoggerFactory.getLogger(ItemServiceIMPL.class);

    @Autowired
    private final ItemDAO itemDAO;

    @Autowired
    private final Mapping mapping;

    @Override
    public void saveItem(ItemDTO itemDTO) {
        logger.info("Saving item with description: {}", itemDTO.getDescription());
        itemDTO.setCode(AppUtil.createItemId());
        ItemEntity savedItem = itemDAO.save(mapping.convertToItemEntity(itemDTO));
        if (savedItem == null) {
            logger.error("Failed to save item: {}", itemDTO);
            throw new DataPersistFailedException("Cannot save data");
        }
        logger.info("Item saved successfully with code: {}", itemDTO.getCode());
    }

    @Override
    public void updateItem(String itemCode, ItemDTO incomeItemDTO) {
        logger.info("Updating item with code: {}", itemCode);
        Optional<ItemEntity> tmpItemEntity= itemDAO.findById(itemCode);
        if(!tmpItemEntity.isPresent()){
            logger.warn("Item with code {} not found", itemCode);
            throw new ItemNotFound("Item not found");
        }else {
            tmpItemEntity.get().setDescription(incomeItemDTO.getDescription());
            tmpItemEntity.get().setPrice(incomeItemDTO.getPrice());
            tmpItemEntity.get().setQty(incomeItemDTO.getQty());
            logger.info("Item with code {} updated successfully", itemCode);
        }
    }

    @Override
    public void deleteItem(String code) {
        logger.info("Deleting item with code: {}", code);
        Optional<ItemEntity> findId = itemDAO.findById(code);
        if(!findId.isPresent()){
            logger.warn("Item with code {} not found", code);
            throw new ItemNotFound("Item not found");
        }else {
            itemDAO.deleteById(code);
            logger.info("Item with code {} deleted successfully", code);
        }
    }

    @Override
    public ItemResponse getSelectedItem(String code) {
        logger.info("Fetching item with code: {}", code);
        if(itemDAO.existsById(code)){
            return mapping.convertToItemDTO(itemDAO.getReferenceById(code));
        }else {
            logger.warn("Item with code {} not found", code);
            return new ItemErrorResponse("0","Item not found");
        }      }

    @Override
    public List<ItemDTO> getAllItems() {
        logger.info("Fetching all items from database");
        return mapping.convertItemListToDTO(itemDAO.findAll());
    }
}
