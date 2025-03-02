package app.integration.repository;

import app.entity.Store;
import app.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class StoreRepositoryTest {

    @Autowired
    private StoreRepository storeRepository;

    private Store testStore;
    private final String storeName = "Test Store";

    @BeforeEach
    public void setUp() {
        testStore = new Store();
        testStore.setId(1L);
        testStore.setName(storeName);

        storeRepository.save(testStore);
    }

    @Test
    public void testExistsByName() {
        assertTrue(storeRepository.existsByName(storeName));
    }

    @Test
    public void testFindById() {
        Optional<Store> foundStore = storeRepository.findById(1L);

        Long foundId = foundStore.get().getId();
        String foundName = foundStore.get().getName();

        assertNotNull(foundName);
        assertEquals("1", foundId.toString());
        assertEquals(storeName, foundName);
    }
}
