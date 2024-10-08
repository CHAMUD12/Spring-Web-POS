package org.example.springwebpos.dao;

import org.example.springwebpos.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemDAO extends JpaRepository<ItemEntity, String> {
    ItemEntity getItemEntityByCode(String code);
}
