package app.unit.service;

import app.dto.InventoryOperationResult;
import app.dto.StoreProductDTO;
import app.enam.InventoryOperationType;
import app.entity.Account;
import app.entity.Inventory;
import app.entity.Product;
import app.entity.Store;
import app.handler.ExceedsStorageCapacityException;
import app.handler.InsufficientBalanceException;
import app.handler.InvalidInputException;
import app.handler.NotFoundException;
import app.repository.AccountRepository;
import app.repository.InventoryRepository;
import app.repository.ProductRepository;
import app.repository.StoreRepository;
import app.service.InventoryService;
import app.utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private StoreRepository storeRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private SecurityUtils securityUtils;
    @InjectMocks
    private InventoryService inventoryService;

    private Account testAccount;
    private Store testStore;
    private Product testProduct;
    private Inventory testInventory;
    private String storeName;
    private String productName;
    private Integer count;
    private List<Inventory> inventories;

    @BeforeEach
    public void setUp() {
        storeName = "Test Store";
        productName = "Яблоко";
        count = 5;

        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setUsername("testUser");
        testAccount.setBalance(new BigDecimal(5000));

        testStore = new Store();
        testStore.setId(1L);
        testStore.setName(storeName);
        testStore.setOwner(testAccount);

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName(productName);
        testProduct.setPrice(new BigDecimal(55));

        testInventory = new Inventory();
        testInventory.setId(1L);
        testInventory.setStore(testStore);
        testInventory.setQuantity(1);
        testInventory.setProduct(testProduct);

        inventories = List.of(testInventory);
    }

    @Test
    public void testGetAllProducts_Success() {
        when(inventoryRepository.findByStoreId(1L)).thenReturn(inventories);

        List<StoreProductDTO> result = inventoryService.getAllProducts(1L);

        assertNotNull(result);
        assertEquals(1, result.size());

        StoreProductDTO dto = result.get(0);
        assertEquals(testProduct.getId(), dto.getId(), "Значение должно быть '1'");
        assertEquals(testProduct.getName(), dto.getName(), "Значение должно быть 'Яблоко'");
        assertEquals(testProduct.getPrice(), dto.getPrice(), "Значение должно быть '55'");
        assertEquals(testInventory.getQuantity(), dto.getQuantity(), "Значение должно быть '1'");

        verify(inventoryRepository, times(1)).findByStoreId(1L);
    }

    @Test
    public void testGetAllProducts_StoreEmpty() {
        when(inventoryRepository.findByStoreId(1L)).thenReturn(List.of());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            inventoryService.getAllProducts(1L);
        });

        assertEquals("Магазин пуст или его не существует", exception.getMessage(),
                "Сообщение должно быть 'Магазин пуст или его не существует'");

        verify(inventoryRepository, times(1)).findByStoreId(1L);
    }

    @Test
    public void testManageProduct_buyProduct() {
        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(storeRepository.findById(1L)).thenReturn(Optional.of(testStore));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(inventoryRepository.findByStoreAndProduct(testStore, testProduct)).thenReturn(Optional.empty());

        Inventory savedInventory = new Inventory();
        savedInventory.setStore(testStore);
        savedInventory.setProduct(testProduct);
        savedInventory.setQuantity(count);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(savedInventory);

        InventoryOperationResult result = inventoryService.manageProduct(
            testStore.getId(), testProduct.getId(), count, InventoryOperationType.BUY_PRODUCT
        );

        assertNotNull(result);
        assertEquals(InventoryOperationType.BUY_PRODUCT, result.getOperationName(),
                "Значение должно быть 'Покупка продукта'");
        assertEquals(testAccount.getUsername(), result.getOwnerName(), "Значение должно быть 'testUser'");
        assertEquals(testStore.getName(), result.getStoreName(), "Значение должно быть 'Test Store'");
        assertEquals(testProduct.getName(), result.getProductName(), "Значение должно быть 'Яблоко'");

        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    public void testManageProduct_sellProduct() {
        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(storeRepository.findById(1L)).thenReturn(Optional.of(testStore));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(inventoryRepository.findByStoreAndProduct(testStore, testProduct)).thenReturn(Optional.empty());

        Inventory savedInventory = new Inventory();
        savedInventory.setStore(testStore);
        savedInventory.setProduct(testProduct);
        savedInventory.setQuantity(count);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(savedInventory);

        InventoryOperationResult result = inventoryService.manageProduct(
                testStore.getId(), testProduct.getId(), count, InventoryOperationType.SELL_PRODUCT
        );

        assertNotNull(result);
        assertEquals(InventoryOperationType.SELL_PRODUCT, result.getOperationName(),
                "Значение должно быть 'Продажа продукта'");
        assertEquals(testAccount.getUsername(), result.getOwnerName(), "Значение должно быть 'testUser'");
        assertEquals(testStore.getName(), result.getStoreName(), "Значение должно быть 'Test Store'");
        assertEquals(testProduct.getName(), result.getProductName(), "Значение должно быть 'Яблоко'");

        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    public void testManageProduct_UserNotFound() {
        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            inventoryService.manageProduct(
                    testStore.getId(), testProduct.getId(), count, InventoryOperationType.BUY_PRODUCT
            );
        });

        assertEquals("Пользователь не найден", exception.getMessage(),
                "Сообщение должно быть 'Пользователь не найден'");

        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    public void testManageProduct_StoreNotFount() {
        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(storeRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            inventoryService.manageProduct(
                    testStore.getId(), testProduct.getId(), count, InventoryOperationType.BUY_PRODUCT
            );
        });

        assertEquals("Магазин не найден", exception.getMessage(),
                "Сообщение должно быть 'Магазин не найден'");

        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    public void testManageProduct_ProductNotFound() {
        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(storeRepository.findById(1L)).thenReturn(Optional.of(testStore));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            inventoryService.manageProduct(
                    testStore.getId(), testProduct.getId(), count, InventoryOperationType.BUY_PRODUCT
            );
        });

        assertEquals("Продукт не найден", exception.getMessage(),
                "Сообщение должно быть 'Продукт не найден'");

        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    public void testManageProduct_ZeroQuantity() {
        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(storeRepository.findById(1L)).thenReturn(Optional.of(testStore));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            inventoryService.manageProduct(
                    testStore.getId(), testProduct.getId(), 0, InventoryOperationType.BUY_PRODUCT
            );
        });

        assertEquals("Количество не может быть равно или меньше нуля", exception.getMessage(),
                "Сообщение должно быть 'Количество не может быть равно или меньше нуля'");

        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    public void testManageProduct_ExcessBalance() {
        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(storeRepository.findById(1L)).thenReturn(Optional.of(testStore));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(inventoryRepository.findByStoreAndProduct(testStore, testProduct)).thenReturn(Optional.empty());

        testAccount.setBalance(new BigDecimal(5));

        InsufficientBalanceException exception = assertThrows(InsufficientBalanceException.class, () -> {
            inventoryService.manageProduct(
                    testStore.getId(), testProduct.getId(), count, InventoryOperationType.BUY_PRODUCT
            );
        });

        assertEquals("Недостаточно средств на балансе для покупки", exception.getMessage(),
                "Сообщение должно быть 'Недостаточно средств на балансе для покупки'");

        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    public void testManageProduct_ExceedsStorage() {
        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(storeRepository.findById(1L)).thenReturn(Optional.of(testStore));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(inventoryRepository.findByStoreAndProduct(testStore, testProduct)).thenReturn(Optional.of(testInventory));

        ExceedsStorageCapacityException exception = assertThrows(ExceedsStorageCapacityException.class, () -> {
            inventoryService.manageProduct(
                    testStore.getId(), testProduct.getId(), 70, InventoryOperationType.BUY_PRODUCT
            );
        });

        assertEquals("Превышена вместимость склада. Текущее количество: " + testInventory.getQuantity() +
                ", максимальная вместимость: 69", exception.getMessage(),
                "Сообщение должно быть 'Превышена вместимость склада. " +
                        "Текущее количество: " + testInventory.getQuantity() + ", максимальная вместимость: 69'");

        verify(inventoryRepository, never()).save(any(Inventory.class));
    }
}