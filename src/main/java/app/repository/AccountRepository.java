package app.repository;

import app.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, java.lang.Long> {
    boolean existsByUsername(String name);
    Optional<Account> findByUsername(String username);
}
