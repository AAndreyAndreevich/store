package app.controller;

import app.dto.InventoryOperationResult;
import app.dto.StoreProductDTO;
import app.handler.*;
import app.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/inv")
public class InventoryController {

    private static final Logger log = LoggerFactory.getLogger(InventoryController.class);

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/getAllProductsForm")
    public String getAllProductsForm() {
        return "getAllProductsForm";
    }

    @GetMapping("/getAllProducts")
    public String getAllProducts(Model model, @RequestParam Long storeId) {
        log.info("Создан запрос на получения списка продуктов из магазина id : {}", storeId);
        try {
            List<StoreProductDTO> products = inventoryService.getAllProducts(storeId);
            model.addAttribute("products", products);
            return "getAllProductsResult";
        } catch (NotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "errorPage";
        }
    }

    @GetMapping("/buyProductForm")
    public String buyProductForm() {
        return "buyProductForm";
    }

    @PostMapping("/buyProduct")
    public String buyProduct(@RequestParam Long storeId,
                             @RequestParam Long productId,
                             @RequestParam Integer count,
                             Model model) {
        log.info("Создан запрос на покупку продукта(-ов) в магазин id : {}", storeId);
        try {
            InventoryOperationResult resultMessage = inventoryService.buyProduct(storeId, productId, count);
            model.addAttribute("resultMessage", resultMessage);
            model.addAttribute("storeId", storeId);
            return "buyProductResult";
        } catch(NotFoundException | AccessDeniedException | InsufficientBalanceException |
                ExceedsStorageCapacityException  | InvalidInputException e) {
            model.addAttribute("error", e.getMessage());
            return "errorPage";
        } catch (Exception e) {
            model.addAttribute("error", "Внутренняя ошибка: " + e.getMessage());
            return "errorPage";
        }
    }

    @GetMapping("/sellProductForm")
    public String sellProductForm() {
        return "sellProductForm";
    }

    @PostMapping("/sellProduct")
    public String sellProduct(@RequestParam Long storeId,
                              @RequestParam Long productId,
                              @RequestParam Integer count,
                              Model model) {
        log.info("Создан запрос на продажу продукта(-ов) из магазина id : {}", storeId);
        try {
            InventoryOperationResult resultMessage = inventoryService.sellProduct(storeId, productId, count);
            model.addAttribute("resultMessage", resultMessage);
            model.addAttribute("storeId", storeId);
            return "sellProductResult";
        } catch (NotFoundException | AccessDeniedException | InsufficientBalanceException |
                 ExceedsStorageCapacityException  | InvalidInputException e) {
            model.addAttribute("error", e.getMessage());
            return "errorPage";
        } catch (Exception e) {
            model.addAttribute("error", "Внутренняя ошибка: " + e.getMessage());
            return "errorPage";
        }
    }
}