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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountDetailsService accountDetailsService;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private SecurityUtils securityUtils;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AccountService accountService;

    private Account testAccount;
    private Account secondAccount;
    private String secondName;
    private String newUsername;
    private String username;
    private String password;
    private String littleUsername;
    private String biggiUsername;

    @BeforeEach
    public void setUp() {
        littleUsername = "min";
        biggiUsername = "BigNameSpecialForThisTest";
        newUsername = "newuser";
        username = "testuser";
        password = "password";

        testAccount = new Account();

        testAccount.setId(1L);
        testAccount.setUsername(username);
        testAccount.setPassword(password);

        secondAccount = new Account();
        secondName = "secend";
        secondAccount.setId(2L);
        secondAccount.setUsername(secondName);
    }

    @Test
    public void register_UsernameIsExistsTest() {
        when(accountRepository.findByUsername(username)).thenReturn(Optional.of(testAccount));

        InvalidUsernameException exception = assertThrows(InvalidUsernameException.class, () -> {
            accountService.register(username, password);
        });

        assertEquals("Пользователь с таким именем уже существует: " + username, exception.getMessage(),
                "Сообщение должно быть 'Пользователь с таким именем уже существует: testuser'");

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void register_UsernameSymbolMoreLimitTest() {
        when(accountRepository.findByUsername(biggiUsername)).thenReturn(Optional.empty());

        InvalidUsernameException exception = assertThrows(InvalidUsernameException.class, () -> {
            accountService.register(biggiUsername, password);
        });

        assertEquals("Имя пользователя должно быть от 4 до 20 символов", exception.getMessage(),
                "Сообщение должно быть 'Имя пользователя должно быть от 4 до 20 символов'");

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void register_UsernameSymbolLessLimitTest() {
        when(accountRepository.findByUsername(littleUsername)).thenReturn(Optional.empty());

        InvalidUsernameException exception = assertThrows(InvalidUsernameException.class, () -> {
            accountService.register(littleUsername, password);
        });

        assertEquals("Имя пользователя должно быть от 4 до 20 символов", exception.getMessage(),
                "Сообщение должно быть 'Имя пользователя должно быть от 4 до 20 символов'");

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void register_PasswordSymbolMoreLimitTest() {
        String bigPass = "passwordISgreetingMEETyouSHOWyourTALANTpoolSWAPMboard";

        when(accountRepository.findByUsername(newUsername)).thenReturn(Optional.empty());

        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () -> {
            accountService.register(newUsername, bigPass);
        });

        assertEquals("Пароль должен быть от 6 до 30 символов", exception.getMessage(),
                "Сообщение должно быть 'Пароль должен быть от 6 до 30 символов'");

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void register_PasswordSymbolLessLimitTest() {
        String minimalisticPassword = "pswrd";

        when(accountRepository.findByUsername(newUsername)).thenReturn(Optional.empty());

        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () -> {
            accountService.register(newUsername, minimalisticPassword);
        });

        assertEquals("Пароль должен быть от 6 до 30 символов", exception.getMessage(),
                "Сообщение должно быть 'Пароль должен быть от 6 до 30 символов'");

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void register_SuccessTest() {
        when(accountRepository.findByUsername(newUsername)).thenReturn(Optional.empty());

        AccountOperationResult result = accountService.register(newUsername, "gggiano");

        assertNotNull(result);

        assertEquals(AccountOperationType.REGISTRATION_ACCOUNT, result.getOperationType(),
                "Должно быть значение 'Регистрация аккаунта'");
        assertEquals(result.getUsername(), newUsername, "Должно быть значение 'newuser'");

        verify(accountRepository, times(1)).findByUsername(newUsername);
    }

    @Test
    public void login_InvalidUsernameInputTest() {
        when(accountRepository.findByUsername(newUsername)).thenReturn(Optional.empty());

        InvalidAuthorizationException exception = assertThrows(InvalidAuthorizationException.class, () -> {
            accountService.login(newUsername, password);
        });

        assertEquals("Пользователя с данным именем не существует: newuser", exception.getMessage(),
                "Сообщение должно быть 'Пользователь с данным именем не существует: newuser'");

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void login_InvalidPasswordInputTest() {
        String wrongPassword = "wrong123";

        when(accountRepository.findByUsername(username)).thenReturn(Optional.of(testAccount));
        when(passwordEncoder.matches(wrongPassword, accountRepository.findByUsername(username).get().getPassword()))
                .thenReturn(false);

        InvalidAuthorizationException exception = assertThrows(InvalidAuthorizationException.class, () -> {
            accountService.login(username, wrongPassword);
        });

        assertEquals("Пароль указан не верно", exception.getMessage(),
                "Пароль указан не верно");

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void login_SuccessTest() {
        when(accountRepository.findByUsername(username)).thenReturn(Optional.of(testAccount));
        when(passwordEncoder.matches(password, accountRepository.findByUsername(username).get().getPassword()))
                .thenReturn(true);

        AccountOperationResult result = accountService.login(username, password);

        assertNotNull(result);

        assertEquals(AccountOperationType.LOG_IN, result.getOperationType(),
                "Должно быть значение 'Авторизация'");
        assertEquals(result.getUsername(), username, "Должно быть значение 'testuser'");

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void changeName_EmptyNewNameInputTest() {
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            accountService.changeName(username, "");
        });

        assertEquals("Имя пользователя не может быть пустым", exception.getMessage(),
                "Сообщение должно быть 'Имя пользователя не может быть пустым'");

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void changeName_EmptyOldNameInputTest() {
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            accountService.changeName("", newUsername);
        });

        assertEquals("Имя пользователя не может быть пустым", exception.getMessage(),
                "Сообщение должно быть 'Имя пользователя не может быть пустым'");

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void changeName_NotFoundUserTest() {
        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(2L);
        when(accountRepository.findByUsername(username)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            accountService.changeName(username, newUsername);
        });

        assertEquals("Пользователь не найден", exception.getMessage(),
                "Сообщение должно быть 'Пользователь не найден'");

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void changeName_AccessDeniedTest() {
        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(1L);
        when(accountRepository.findByUsername(secondName)).thenReturn(Optional.of(secondAccount));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            accountService.changeName(secondName, newUsername);
        });

        assertEquals("Пользователю не принадлежит это имя", exception.getMessage(),
                "Сообщение должно быть 'Пользователю не принадлежит это имя'");

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void changeName_UsernameSymbolMoreLimitTest() {
        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(1L);
        when(accountRepository.findByUsername(username)).thenReturn(Optional.of(testAccount));

        InvalidUsernameException exception = assertThrows(InvalidUsernameException.class, () -> {
            accountService.changeName(username, biggiUsername);
        });

        assertEquals("Имя пользователя должно быть от 4 до 20 символов", exception.getMessage(),
                "Сообщение должно быть 'Имя пользователя должно быть от 4 до 20 символов'");

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void changeName_UsernameSymbolLessLimitTest() {
        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(1L);
        when(accountRepository.findByUsername(username)).thenReturn(Optional.of(testAccount));

        InvalidUsernameException exception = assertThrows(InvalidUsernameException.class, () -> {
            accountService.changeName(username, littleUsername);
        });

        assertEquals("Имя пользователя должно быть от 4 до 20 символов", exception.getMessage(),
                "Сообщение должно быть 'Имя пользователя должно быть от 4 до 20 символов'");

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void changeName_UsernameIsExistsTest() {
        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(1L);
        when(accountRepository.findByUsername(username)).thenReturn(Optional.of(testAccount));
        when(accountRepository.existsByUsername(secondName)).thenReturn(true);

        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            accountService.changeName(username, secondName);
        });

        assertEquals("Имя пользователя уже занято", exception.getMessage(),
                "Сообщение должно быть 'Имя пользователя уже занято'");

        verify(accountRepository, never()).save(any(Account.class));
    }
//я сплю, завтра переделаю
    @Test
    public void changeName_SuccessTest() {
        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(1L);
        when(accountRepository.findByUsername(username)).thenReturn(Optional.of(testAccount));
        when(accountRepository.existsByUsername(newUsername)).thenReturn(false);

        AccountOperationResult result = accountService.changeName(username, newUsername);

        assertNotNull(result);

        assertEquals(AccountOperationType.CHANGE_USERNAME, result.getOperationType(),
                "Значение должно быть 'Смена имени'");
        assertEquals("testuser -> newuser", result.getUsername(),
                "Значение должно быть 'testuser -> newuser'");

        verify(accountRepository, times(1)).findByUsername(newUsername);
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