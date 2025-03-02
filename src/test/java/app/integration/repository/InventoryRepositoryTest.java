package app.integration.repository;

import app.entity.Inventory;
import app.entity.Product;
import app.entity.Store;
import app.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class InventoryRepositoryTest {

    @Autowired
    private InventoryRepository inventoryRepository;

//    List<Inventory> findByStoreId(Long storeId);
//    Optional<Inventory> findByStoreAndProduct(Store store, Product product);
}
