package app.unit.service;

import app.dto.AccountOperationResult;
import app.enam.AccountOperationType;
import app.entity.Account;
import app.handler.*;
import app.repository.AccountRepository;
import app.service.AccountDetailsService;
import app.service.AccountService;
import app.utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Autowired
    private AccountDetailsService accountDetailsService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private SecurityUtils securityUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AccountService accountService;

    private Account testAccount;
    private String username;
    private String password;

    @BeforeEach
    public void setUp() {
        username = "testuser";
        password = "password";

        testAccount.setUsername(username);
        testAccount.setPassword(passwordEncoder.encode(password));
    }

    @Test
    public void register_UsernameIsExistsTest() {
        when(accountRepository.findByUsername(username)).thenReturn(Optional.of(testAccount));

        InvalidUsernameException exception = assertThrows(InvalidUsernameException.class, () -> {
            accountService.register(username, password);
        });

        assertEquals("Пользователь с таким именем уже существует: " + username, exception.getMessage(),
                "Сообщение должно быть 'Пользователь с таким именем уже существует: testuser'");
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

    @Test
    public void changeName_EmptyNewNameInputTest() {

    }

    @Test
    public void changeName_EmptyOldNameInputTest() {

    }

    @Test
    public void changeName_NotFoundUserTest() {

    }

    @Test
    public void changeName_AccessDeniedTest() {

    }

    @Test
    public void changeName_UsernameSymbolMoreLimitTest() {

    }

    @Test
    public void changeName_UsernameSymbolLessLimitTest() {

    }

    @Test
    public void changeName_UsernameIsExistsTest() {

    }

    @Test
    public void changeName_SuccessTest() {

    }

    @Test
    public void changePassword_EmptyInputTest() {

    }

    @Test
    public void changePassword_NotFountUserTest() {

    }

    @Test
    public void changePassword_InvalidCurrentPasswordTest() {

    }

    @Test
    public void changePassword_NewPasswordEqualsOldPasswordTest() {

    }

    @Test
    public void changePassword_PasswordSymbolMoreLimitTest() {

    }

    @Test
    public void changePassword_PasswordSymbolLessLimitTest() {

    }

    @Test
    public void changePassword_SuccessTest() {

    }
}