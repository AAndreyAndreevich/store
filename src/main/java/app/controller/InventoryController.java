package app.controller;

import app.dto.InventoryOperationResult;
import app.dto.StoreProductDTO;
import app.enam.InventoryOperationType;
import app.entity.Store;
import app.handler.*;
import app.repository.StoreRepository;
import app.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/inv")
public class InventoryController {

    private static final Logger log = LoggerFactory.getLogger(InventoryController.class);

    private final InventoryService inventoryService;
    private final StoreRepository storeRepository;

    @Autowired
    public InventoryController(InventoryService inventoryService, StoreRepository storeRepository) {
        this.inventoryService = inventoryService;
        this.storeRepository = storeRepository;
    }

    @GetMapping("/getAllProductsForm")
    public String getAllProductsForm(Model model) {
        List<Store> stores = storeRepository.findAll();
        model.addAttribute("stores", stores);
        model.addAttribute("products", Collections.emptyList());
        return "getAllProductsForm";
    }

    @GetMapping("/getAllProducts")
    public ResponseEntity<?> getAllProducts(@RequestParam Long storeId) {
        log.info("Создан запрос на получения списка продуктов из магазина id : {}", storeId);
        try {
            List<StoreProductDTO> products = inventoryService.getAllProducts(storeId);
            return ResponseEntity.ok(products);
        } catch (NotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Внутренняя ошибка: " + e.getMessage());
        }
    }

    @GetMapping("/manageProductForm")
    public String manageProductForm(Model model) {
        model.addAttribute("operationTypes", InventoryOperationType.values());
        return "manageProductForm";
    }

    @PostMapping("/manageProduct")
    public ResponseEntity<?> manageProduct(
            @RequestParam Long storeId,
            @RequestParam Long productId,
            @RequestParam Integer count,
            @RequestParam InventoryOperationType operationType
            ) {
        try {
            InventoryOperationResult result = inventoryService.manageProduct(storeId, productId, count, operationType);
            return ResponseEntity.ok(result);
        } catch (NotFoundException | AccessDeniedException | InsufficientBalanceException |
                 ExceedsStorageCapacityException  | InvalidInputException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Внутренняя ошибка: " + e.getMessage());
        }
    }
}