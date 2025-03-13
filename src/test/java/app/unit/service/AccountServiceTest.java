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
    private String newPassword;
    private String username;
    private String password;
    private String littleUsername;
    private String biggiUsername;
    private String bigPass;
    private String minimalisticPassword;
    private String errorMessage;
    private String currentErrorMessage;

    @BeforeEach
    public void setUp() {
        littleUsername = "min";
        biggiUsername = "BigNameSpecialForThisTest";
        bigPass = "passwordISgreetingMEETyouSHOWyourTALANTpoolSWAPMboard";
        minimalisticPassword = "pswrd";
        newUsername = "newuser";
        newPassword = "newpass";
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

        currentErrorMessage = "Сообщение должно быть : " + errorMessage;
    }

    @Test
    public void register_UsernameIsExistsTest() {
        errorMessage = "Пользователь с таким именем уже существует: " + username;
        when(accountRepository.findByUsername(username)).thenReturn(Optional.of(testAccount));

        InvalidUsernameException exception = assertThrows(InvalidUsernameException.class, () -> {
            accountService.registration(username, password);
        });

        assertEquals(errorMessage, exception.getMessage(), currentErrorMessage);

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void register_UsernameSymbolMoreLimitTest() {
        errorMessage = "Имя пользователя должно быть от 4 до 20 символов";
        when(accountRepository.findByUsername(biggiUsername)).thenReturn(Optional.empty());

        InvalidUsernameException exception = assertThrows(InvalidUsernameException.class, () -> {
            accountService.registration(biggiUsername, password);
        });

        assertEquals(errorMessage, exception.getMessage(), currentErrorMessage);

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void register_UsernameSymbolLessLimitTest() {
        errorMessage = "Имя пользователя должно быть от 4 до 20 символов";
        when(accountRepository.findByUsername(littleUsername)).thenReturn(Optional.empty());

        InvalidUsernameException exception = assertThrows(InvalidUsernameException.class, () -> {
            accountService.registration(littleUsername, password);
        });

        assertEquals(errorMessage, exception.getMessage(), currentErrorMessage);

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void register_PasswordSymbolMoreLimitTest() {
        errorMessage = "Пароль должен быть от 6 до 30 символов";
        when(accountRepository.findByUsername(newUsername)).thenReturn(Optional.empty());

        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () -> {
            accountService.registration(newUsername, bigPass);
        });

        assertEquals(errorMessage, exception.getMessage(), currentErrorMessage);

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void register_PasswordSymbolLessLimitTest() {
        errorMessage = "Пароль должен быть от 6 до 30 символов";
        when(accountRepository.findByUsername(newUsername)).thenReturn(Optional.empty());

        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () -> {
            accountService.registration(newUsername, minimalisticPassword);
        });

        assertEquals(errorMessage, exception.getMessage(), currentErrorMessage);

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void register_SuccessTest() {
        when(accountRepository.findByUsername(newUsername)).thenReturn(Optional.empty());

        AccountOperationResult result = accountService.registration(newUsername, "gggiano");

        assertNotNull(result);

        assertEquals(AccountOperationType.REGISTRATION_ACCOUNT, result.getOperationType(),
                "Должно быть значение 'Регистрация аккаунта'");
        assertEquals(result.getUsername(), newUsername, "Должно быть значение 'newuser'");

        verify(accountRepository, times(1)).findByUsername(newUsername);
    }

    @Test
    public void login_InvalidUsernameInputTest() {
        errorMessage = "Пользователя с данным именем не существует: " + newUsername;
        when(accountRepository.findByUsername(newUsername)).thenReturn(Optional.empty());

        InvalidAuthorizationException exception = assertThrows(InvalidAuthorizationException.class, () -> {
            accountService.login(newUsername, password);
        });

        assertEquals(errorMessage, exception.getMessage(), currentErrorMessage);

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void login_InvalidPasswordInputTest() {
        errorMessage = "Пароль указан не верно";
        String wrongPassword = "wrong123";

        when(accountRepository.findByUsername(username)).thenReturn(Optional.of(testAccount));
        when(passwordEncoder.matches(wrongPassword, accountRepository.findByUsername(username).get().getPassword()))
                .thenReturn(false);

        InvalidAuthorizationException exception = assertThrows(InvalidAuthorizationException.class, () -> {
            accountService.login(username, wrongPassword);
        });

        assertEquals(errorMessage, exception.getMessage(), currentErrorMessage);

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
        errorMessage = "Имя пользователя не может быть пустым";
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            accountService.changeName(username, "");
        });

        assertEquals(errorMessage, exception.getMessage(), currentErrorMessage);

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void changeName_EmptyOldNameInputTest() {
        errorMessage = "Имя пользователя не может быть пустым";
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            accountService.changeName("", newUsername);
        });

        assertEquals(errorMessage, exception.getMessage(), currentErrorMessage);

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void changeName_NotFoundUserTest() {
        errorMessage = "Пользователь не найден";
        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(2L);
        when(accountRepository.findByUsername(username)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            accountService.changeName(username, newUsername);
        });

        assertEquals(errorMessage, exception.getMessage(), currentErrorMessage);

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void changeName_AccessDeniedTest() {
        errorMessage = "Пользователю не принадлежит это имя";
        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(1L);
        when(accountRepository.findByUsername(secondName)).thenReturn(Optional.of(secondAccount));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            accountService.changeName(secondName, newUsername);
        });

        assertEquals(errorMessage, exception.getMessage(), currentErrorMessage);

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void changeName_UsernameSymbolMoreLimitTest() {
        errorMessage = "Имя пользователя должно быть от 4 до 20 символов";
        InvalidUsernameException exception = assertThrows(InvalidUsernameException.class, () -> {
            accountService.changeName(username, biggiUsername);
        });

        assertEquals(errorMessage, exception.getMessage(), currentErrorMessage);

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void changeName_UsernameSymbolLessLimitTest() {
        errorMessage = "Имя пользователя должно быть от 4 до 20 символов";
        InvalidUsernameException exception = assertThrows(InvalidUsernameException.class, () -> {
            accountService.changeName(username, littleUsername);
        });

        assertEquals(errorMessage, exception.getMessage(), currentErrorMessage);

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void changeName_UsernameIsExistsTest() {
        errorMessage = "Имя пользователя уже занято";
        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(1L);
        when(accountRepository.findByUsername(username)).thenReturn(Optional.of(testAccount));
        when(accountRepository.existsByUsername(secondName)).thenReturn(true);

        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            accountService.changeName(username, secondName);
        });

        assertEquals(errorMessage, exception.getMessage(), currentErrorMessage);

        verify(accountRepository, never()).save(any(Account.class));
    }

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

        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    public void changePassword_EmptyOldPasswordInputTest() {
        errorMessage = "Пароль не может быть пустым";
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            accountService.changePassword("", newPassword);
        });

        assertEquals(errorMessage, exception.getMessage(), currentErrorMessage);

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void changePassword_EmptyNewPasswordInputTest() {
        errorMessage = "Пароль не может быть пустым";
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            accountService.changePassword(password, "");
        });

        assertEquals(errorMessage, exception.getMessage(), currentErrorMessage);

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void changePassword_NotFountUserTest() {
        errorMessage = "Пользователь не найден";
        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(3L);
        when(accountRepository.findById(3L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            accountService.changePassword(password, newPassword);
        });

        assertEquals(errorMessage, exception.getMessage(), currentErrorMessage);

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void changePassword_InvalidCurrentPasswordTest() {
        errorMessage = "Неправильно указан действующий пароль";
        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(passwordEncoder.matches(newPassword, testAccount.getPassword())).thenReturn(false);

        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () -> {
            accountService.changePassword(newPassword, password);
        });

        assertEquals(errorMessage, exception.getMessage(), currentErrorMessage);

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void changePassword_NewPasswordEqualsOldPasswordTest() {
        errorMessage = "Новый пароль должен отличаться от старого";
        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(passwordEncoder.matches(password, testAccount.getPassword())).thenReturn(true);

        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () -> {
            accountService.changePassword(password, password);
        });

        assertEquals(errorMessage, exception.getMessage(), currentErrorMessage);

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void changePassword_PasswordSymbolMoreLimitTest() {
        errorMessage = "Пароль должен быть от 6 до 30 символов";
        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () -> {
            accountService.changePassword(password, bigPass);
        });

        assertEquals(errorMessage, exception.getMessage(), currentErrorMessage);

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void changePassword_PasswordSymbolLessLimitTest() {
        errorMessage = "Пароль должен быть от 6 до 30 символов";
        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () -> {
            accountService.changePassword(password, minimalisticPassword);
        });

        assertEquals(errorMessage, exception.getMessage(), currentErrorMessage);

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void changePassword_SuccessTest() {
        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(passwordEncoder.matches(password, testAccount.getPassword())).thenReturn(true);

        AccountOperationResult result = accountService.changePassword(password, newPassword);

        assertNotNull(result);

        assertEquals(AccountOperationType.CHANGE_PASSWORD, result.getOperationType(),
                "Значение должно быть 'Смена пароля'");
        assertEquals(result.getUsername(), username, "Значение должно быть 'testuser'");

        verify(accountRepository, times(1)).save(any(Account.class));
    }
}