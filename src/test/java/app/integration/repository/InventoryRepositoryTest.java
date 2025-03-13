package app.integration.repository;

import app.entity.Inventory;
import app.entity.Product;
import app.entity.Store;
import app.repository.InventoryRepository;
import app.repository.ProductRepository;
import app.repository.StoreRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Tag("Integration")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class InventoryRepositoryTest {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void findByStoreIdTest() {
        Store store = new Store();
        store.setName("Store 1");
        storeRepository.save(store);

        Product product = new Product();
        product.setName("Product 1");
        productRepository.save(product);

        Inventory inventory = new Inventory();
        inventory.setStore(store);
        inventory.setProduct(product);
        inventory.setQuantity(10);
        inventoryRepository.save(inventory);

        List<Inventory> inventories = inventoryRepository.findByStoreId(store.getId());

        assertFalse(inventories.isEmpty());
        assertEquals(1, inventories.size());
        assertEquals(store.getId(), inventories.get(0).getStore().getId());
    }

    @Test
    public void findByStoreAndProductTest() {
        Store store = new Store();
        store.setName("Store 1");
        storeRepository.save(store);

        Product product = new Product();
        product.setName("Product 1");
        productRepository.save(product);

        Inventory inventory = new Inventory();
        inventory.setStore(store);
        inventory.setProduct(product);
        inventory.setQuantity(10);
        inventoryRepository.save(inventory);

        Optional<Inventory> foundInventory = inventoryRepository.findByStoreAndProduct(store, product);

        assertTrue(foundInventory.isPresent());
        assertEquals(store.getId(), foundInventory.get().getStore().getId());
        assertEquals(product.getId(), foundInventory.get().getProduct().getId());
    }
}
