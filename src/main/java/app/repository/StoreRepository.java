package app.repository;

import app.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {
    boolean existsByName(String name);
    @Override
    Optional<Store> findById(Long storeId);
}