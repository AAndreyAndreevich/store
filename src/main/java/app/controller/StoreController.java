package app.controller;

import app.dto.StoreOperationResult;
import app.handler.AccessDeniedException;
import app.handler.AlreadyExistsException;
import app.handler.InvalidInputException;
import app.handler.NotFoundException;
import app.service.StoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/store")
public class StoreController {

    private static final Logger log = LoggerFactory.getLogger(StoreController.class);

    private final StoreService storeService;

    @Autowired
    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping("/createStoreForm")
    public String createStoreForm() {
        return "createStoreForm";
    }

    @PostMapping("/createStore")
    public String createStore(@RequestParam String storeName, Model model) {
        log.info("Создан запрос на создание магазина с именем : '{}'", storeName );
        try {
            StoreOperationResult resultMessage = storeService.createStore(storeName);
            model.addAttribute("resultMessage", resultMessage);
            model.addAttribute("storeName", storeName);
            return "createStoreResult";
        } catch (NotFoundException | InvalidInputException | AlreadyExistsException e) {
            model.addAttribute("error", e.getMessage());
            return "errorPage";
        } catch (Exception e) {
            model.addAttribute("error", "Внутренняя ошибка: " + e.getMessage());
            return "errorPage";
        }
    }

    @GetMapping("/deleteStoreForm")
    public String deleteStoreForm() {
        return "deleteStoreForm";
    }

    @DeleteMapping("/deleteStore")
    public String deleteStore(@RequestParam Long storeId, Model model) {
        log.info("Создан запрос на удаление магазина id : '{}'", storeId);
        try {
            StoreOperationResult resultMessage = storeService.deleteStore(storeId);
            model.addAttribute("resultMessage", resultMessage);
            model.addAttribute("storeId", storeId);
            return "deleteStoreResult";
        } catch (NotFoundException | AccessDeniedException e) {
            model.addAttribute("error", e.getMessage());
            return "errorPage";
        } catch (Exception e) {
            model.addAttribute("error", "Внутренняя ошибка: " + e.getMessage());
            return "errorPage";
        }
    }

}