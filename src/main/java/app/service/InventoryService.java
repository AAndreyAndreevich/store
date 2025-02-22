package app.service;

import app.dto.InventoryOperationResult;
import app.dto.StoreProductDTO;
import app.enam.InventoryOperationType;
import app.entity.Account;
import app.entity.Inventory;
import app.entity.Product;
import app.entity.Store;
import app.handler.*;
import app.repository.AccountRepository;
import app.repository.InventoryRepository;
import app.repository.ProductRepository;
import app.repository.StoreRepository;
import app.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);
    private final AccountRepository accountRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final InventoryRepository inventoryRepository;
    private final SecurityUtils securityUtils;

    @Autowired
    public InventoryService(AccountRepository accountRepository, ProductRepository productRepository,
                            StoreRepository storeRepository, InventoryRepository inventoryRepository,
                            SecurityUtils securityUtils) {
        this.accountRepository = accountRepository;
        this.productRepository = productRepository;
        this.storeRepository = storeRepository;
        this.inventoryRepository = inventoryRepository;
        this.securityUtils = securityUtils;
    }

    public List<StoreProductDTO> getAllProducts(Long storeId) {
        List<Inventory> inventories = inventoryRepository.findByStoreId(storeId);
        if (inventories.isEmpty()) {
            throw new NotFoundException("Магазин пуст или его не существует");
        }
        return inventories.stream()
                .map(inventory -> new StoreProductDTO(
                        inventory.getProduct().getId(), inventory.getProduct().getName(),
                        inventory.getProduct().getPrice(), inventory.getQuantity()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public InventoryOperationResult manageProduct(Long storeId, Long productId,
                                                  Integer count, InventoryOperationType operationType) {
        Long accountId = securityUtils.getCurrentUserId(accountRepository);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException("Магазин не найден"));

        validateStoreOwnership(storeId, account);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Продукт не найден"));

        if (count <= 0) {
            throw new InvalidInputException("Количество не может быть равно или меньше нуля");
        }

        BigDecimal totalCost = product.getPrice().multiply(BigDecimal.valueOf(count));

        Inventory inventory = inventoryRepository.findByStoreAndProduct(store, product)
                .orElseGet(() -> createNewInventory(store, product));

        if (operationType.equals(InventoryOperationType.BUY_PRODUCT)) {
            if (inventory.getQuantity() + count > 69) {
                throw new ExceedsStorageCapacityException(
                        "Превышена вместимость склада. Текущее количество: " + inventory.getQuantity() +
                                ", максимальная вместимость: 69"
                );
            }
            inventory.setQuantity(inventory.getQuantity() + count);
            account.setBalance(account.getBalance().subtract(totalCost));
        } else if (operationType.equals(InventoryOperationType.SELL_PRODUCT)) {
            if (inventory.getQuantity() - count < 0) {
                throw new ExceedsStorageCapacityException(
                        "Превышен лимит количества продукта. Текущее количество: " + inventory.getQuantity()
                );
            }
            inventory.setQuantity(inventory.getQuantity() - count);
            account.setBalance(account.getBalance().add(totalCost));
        } else {
            throw new InvalidInputException("Неизвестный тип операции");
        }
        log.info("В магазине '{}' произошла операция '{}' над продуктом '{}' в количестве {} штук",
                store.getName(), operationType, product.getName(), count);
        return new InventoryOperationResult(
                operationType,
                account.getBalance(),
                product.getName(),
                count,
                account.getUsername(),
                store.getName(),
                true);
    }

    private Inventory createNewInventory(Store store, Product product) {
        Inventory inventory = new Inventory();
        inventory.setStore(store);
        inventory.setProduct(product);
        inventory.setQuantity(0);
        return inventoryRepository.save(inventory);
    }

    private void validateStoreOwnership(Long storeId, Account account) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException("Магазин не найден"));
        if (!store.getOwner().equals(account)) {
            throw new AccessDeniedException("Пользователю не принадлежит магазин");
        }
    }
}