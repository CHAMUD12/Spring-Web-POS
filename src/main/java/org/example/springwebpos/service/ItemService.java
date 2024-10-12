package org.example.springwebpos.service;

import org.example.springwebpos.customObj.ItemResponse;
import org.example.springwebpos.dto.ItemDTO;

import java.util.List;

public interface ItemService {
    void saveItem(ItemDTO itemDTO);

    void updateItem(String itemCode, ItemDTO itemDTO);

    void deleteItem(String code);

    ItemResponse getSelectedItem(String code);

    List<ItemDTO> getAllItems();
}
