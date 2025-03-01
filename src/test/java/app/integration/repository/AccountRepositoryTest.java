package app.integration.repository;

import app.entity.Account;
import app.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void testFindByUsername() {
        Account account = new Account("testuser", "testpass");

        accountRepository.save(account);

        Optional<Account> foundAccount = accountRepository.findByUsername("testuser");

        String username = foundAccount.get().getUsername();
        String password = foundAccount.get().getPassword();

        assertNotNull(foundAccount);
        assertEquals("testuser", username);
        assertEquals("testpass", password);
    }

}