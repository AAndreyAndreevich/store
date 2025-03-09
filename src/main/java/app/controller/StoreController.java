package app.controller;

import app.dto.StoreOperationResult;
import app.enam.StoreOperationType;
import app.entity.Store;
import app.handler.AccessDeniedException;
import app.handler.AlreadyExistsException;
import app.handler.InvalidInputException;
import app.handler.NotFoundException;
import app.service.StoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> createStore(@RequestParam String storeName) {
        log.info("Создан запрос на создание магазина с именем : '{}'", storeName );
        try {
            StoreOperationResult result = storeService.createStore(storeName);
            return ResponseEntity.ok(result);
        } catch (NotFoundException | InvalidInputException | AlreadyExistsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Внутренняя ошибка: " + e.getMessage());
        }
    }

    @GetMapping("/changeStoreNameForm")
    public String changeStoreNameForm(Model model) {
        model.addAttribute("store", new Store());
        return "changeStoreName";
    }

    @PostMapping("/changeStoreName")
    public ResponseEntity<?> changeStoreName(@RequestParam String oldName, @RequestParam String newName) {
        try {
            StoreOperationResult result = storeService.changeName(oldName, newName);
            return ResponseEntity.ok(result);
        } catch (NotFoundException | InvalidInputException | AlreadyExistsException | AccessDeniedException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Внутренняя ошибка: " + e.getMessage());
        }
    }

}