package app.unit.service;

import app.dto.AccountOperationResult;
import app.enam.AccountOperationType;
import app.entity.Account;
import app.handler.InvalidAuthorizationException;
import app.handler.InvalidPasswordException;
import app.handler.InvalidUsernameException;
import app.repository.AccountRepository;
import app.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Autowired
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private String username;
    private String password;

    @BeforeEach
    public void setUp() {
        username = "testuser";
        password = "password";
    }

    @Test
    public void register_UsernameIsExistsTest() {

    }

    @Test
    public void register_UsernameSymbolMoreLimitTest() {

    }

    @Test
    public void register_UsernameSymbolLessLimitTest() {

    }

    @Test
    public void register_PasswordSymbolMoreLimitTest() {

    }

    @Test
    public void register_PasswordSymbolLessLimitTest() {

    }

    @Test
    public void register_SuccessTest() {

    }

    @Test
    public void login_InvalidUsernameInputTest() {

    }

    @Test
    public void login_InvalidPasswordInputTest() {

    }

    @Test
    public void login_SuccessTest() {

    }
}