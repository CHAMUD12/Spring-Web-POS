package org.example.springwebpos.controller;

import lombok.RequiredArgsConstructor;
import org.example.springwebpos.customObj.ItemErrorResponse;
import org.example.springwebpos.customObj.ItemResponse;
import org.example.springwebpos.dto.ItemDTO;
import org.example.springwebpos.exception.DataPersistFailedException;
import org.example.springwebpos.exception.ItemNotFound;
import org.example.springwebpos.service.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemErrorResponse> createItem(@RequestBody ItemDTO item) {
        if (item == null) {
            logger.warn("Item is null");
            return new ResponseEntity<>(new ItemErrorResponse("error", "Item cannot be null"),
                    HttpStatus.BAD_REQUEST);
        }

        // Validate Description
        if (item.getDescription() == null || item.getDescription().isEmpty()) {
            logger.warn("Item description is missing");
            return new ResponseEntity<>(new ItemErrorResponse("error", "Description is required"),
                    HttpStatus.BAD_REQUEST);
        }
        if (item.getDescription().length() > 50) {
            logger.warn("Item description exceeds max length: {}", item.getDescription());
            return new ResponseEntity<>(new ItemErrorResponse("error",
                    "Description must be 50 characters or less"), HttpStatus.BAD_REQUEST);
        }

        // Validate Price
        if (item.getPrice() <= 0) {
            logger.warn("Invalid price: Price must be a positive number");
            return new ResponseEntity<>(new ItemErrorResponse("error",
                    "Price must be a positive number"), HttpStatus.BAD_REQUEST);
        }

        // Validate that price does not contain letters
        if (Double.toString(item.getPrice()).matches(".[a-zA-Z]+.")) {
            logger.warn("Invalid price format: Price should not contain letters");
            return new ResponseEntity<>(new ItemErrorResponse("error",
                    "Price must be a valid number without letters"), HttpStatus.BAD_REQUEST);
        }

        // Validate Quantity
        if (item.getQty() < 0) {
            logger.warn("Invalid quantity: Quantity cannot be negative");
            return new ResponseEntity<>(new ItemErrorResponse("error",
                    "Quantity cannot be negative"), HttpStatus.BAD_REQUEST);
        }

        // Validate that quantity does not contain letters
        if (Integer.toString(item.getQty()).matches(".[a-zA-Z]+.")) {
            logger.warn("Invalid quantity format: Quantity should not contain letters");
            return new ResponseEntity<>(new ItemErrorResponse("error",
                    "Quantity must be a valid number without letters"), HttpStatus.BAD_REQUEST);
        }

        try {
            // Save the item
            itemService.saveItem(item);
            logger.info("Item created successfully: {}", item);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (DataPersistFailedException e) {
            logger.error("Data persistence failed: {}", e.getMessage());
            return new ResponseEntity<>(new ItemErrorResponse("error", "Data persistence failed"),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Internal server error: {}", e.getMessage());
            return new ResponseEntity<>(new ItemErrorResponse("error", "Internal server error"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping(value = "/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemErrorResponse> updateItem(
            @PathVariable("code") String itemCode, @RequestBody ItemDTO item) {
        try {
            if (item == null || itemCode == null || itemCode.isEmpty()) {
                logger.warn("Invalid update request: itemCode={}, item={}", itemCode, item);
                return new ResponseEntity<>(new ItemErrorResponse("error",
                        "Item and itemCode cannot be null or empty"), HttpStatus.BAD_REQUEST);
            }
            if (item.getDescription() == null || item.getDescription().isEmpty()) {
                logger.warn("Item description is missing during update for itemCode: {}", itemCode);
                return new ResponseEntity<>(new ItemErrorResponse("error",
                        "Description is required"), HttpStatus.BAD_REQUEST);
            }
            if (item.getDescription().length() > 50) {
                logger.warn("Item description exceeds max length for itemCode: {}", itemCode);
                return new ResponseEntity<>(new ItemErrorResponse("error",
                        "Description must be 50 characters or less"), HttpStatus.BAD_REQUEST);
            }
            if (item.getPrice() <= 0) {
                logger.warn("Invalid price: Price must be a positive number for itemCode: {}", itemCode);
                return new ResponseEntity<>(new ItemErrorResponse("error",
                        "Price must be a positive number"), HttpStatus.BAD_REQUEST);
            }
            if (Double.toString(item.getPrice()).matches(".*[a-zA-Z]+.*")) {
                logger.warn("Invalid price format: Price should not contain letters for itemCode: {}", itemCode);
                return new ResponseEntity<>(new ItemErrorResponse("error",
                        "Price must be a valid number without letters"), HttpStatus.BAD_REQUEST);
            }
            if (item.getQty() < 0) {
                logger.warn("Invalid quantity: Quantity cannot be negative for itemCode: {}", itemCode);
                return new ResponseEntity<>(new ItemErrorResponse("error",
                        "Quantity cannot be negative"), HttpStatus.BAD_REQUEST);
            }
            if (Integer.toString(item.getQty()).matches(".*[a-zA-Z]+.*")) {
                logger.warn("Invalid quantity format: Quantity should not contain letters for itemCode: {}", itemCode);
                return new ResponseEntity<>(new ItemErrorResponse("error",
                        "Quantity must be a valid number without letters"), HttpStatus.BAD_REQUEST);
            }
            itemService.updateItem(itemCode, item);
            logger.info("Item updated successfully: {}", itemCode);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        } catch (ItemNotFound e) {
            logger.error("Item not found: {}", itemCode);
            return new ResponseEntity<>(new ItemErrorResponse("error",
                    "Item not found"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Internal server error while updating item: {}", e.getMessage());
            return new ResponseEntity<>(new ItemErrorResponse("error",
                    "Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value = "/{code}")
    public ResponseEntity<Void> deleteItem(@PathVariable("code") String itemCode) {
        try {
            itemService.deleteItem(itemCode);
            logger.info("Item with code {} deleted successfully", itemCode);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ItemNotFound e) {
            logger.warn("Item with code {} not found", itemCode);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("An unexpected error occurred while deleting item with code {}", itemCode, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ItemResponse getSelectedItem(@PathVariable("code") String code) {
        logger.info("Fetching item with code {}", code);
        return itemService.getSelectedItem(code);
    }

    @GetMapping(value = "allitems", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ItemDTO> getAllItems() {
        logger.info("Fetching all items");
        return itemService.getAllItems();
    }
}
