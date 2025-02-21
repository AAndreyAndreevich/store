package app.repository;

import app.entity.Inventory;
import app.entity.Product;
import app.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findByStoreId(Long storeId);
    Optional<Inventory> findByStoreAndProduct(Store store, Product product);

}