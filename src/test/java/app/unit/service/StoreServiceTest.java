package app.unit.service;

import app.dto.StoreOperationResult;
import app.enam.StoreOperationType;
import app.entity.Account;
import app.entity.Store;
import app.handler.AccessDeniedException;
import app.handler.AlreadyExistsException;
import app.handler.InvalidInputException;
import app.handler.NotFoundException;
import app.repository.AccountRepository;
import app.repository.StoreRepository;
import app.service.StoreService;
import app.utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private SecurityUtils securityUtils;
    @InjectMocks
    private StoreService storeService;

    private Account testAccount;
    private Store testStore;
    private String storeName;
    private String userName;
    private String errorMessage;
    private String currentErrorMessage;

    @BeforeEach
    public void setUp() {
        currentErrorMessage = "Сообщение должно быть : " + errorMessage;
        storeName = "Test Store";
        userName = "testUser";

        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setUsername(userName);

        testStore = new Store();
        testStore.setName(storeName);
        testStore.setOwner(testAccount);
        testStore.setId(1L);
    }

    @Test
    public void createStore_SuccessTest() {
        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(storeRepository.existsByName(storeName)).thenReturn(false);

        StoreOperationResult result = storeService.createStore(storeName);

        assertNotNull(result);
        assertEquals(StoreOperationType.CREATE, result.getOperationName(), "Должен вернуть название операции");
        assertEquals(testAccount.getUsername(), result.getOwnerName(), "Должен вернуть имя пользователя");
        assertEquals(storeName, result.getStoreName(), "Должен вернуть название магазина");

        verify(storeRepository, times(1)).save(any(Store.class));
    }

    @Test
    public void createStore_EmptyStoreNameTest() {
        errorMessage = "Название магазина не может быть пустым";
        String emptyStoreName = "";

        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            storeService.createStore(emptyStoreName);
        });

        assertEquals(errorMessage, exception.getMessage(), currentErrorMessage);

        verify(storeRepository, never()).save(any(Store.class));
    }

    @Test
    public void createStore_StoreAlreadyExistsTest() {
        errorMessage = "Магазин с названием 'Test Store' существует";
        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(storeRepository.existsByName(storeName)).thenReturn(true);

        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class, () -> {
            storeService.createStore(storeName);
        });

        assertEquals(errorMessage, exception.getMessage(), currentErrorMessage);

        verify(storeRepository, never()).save(any(Store.class));
    }

    @Test
    public void createStore_UserNotFountTest() {
        errorMessage = "Пользователь не найден";
        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
           storeService.createStore(storeName);
        });

        assertEquals(errorMessage, exception.getMessage(), currentErrorMessage);

        verify(storeRepository, never()).save(any(Store.class));
    }

    @Test
    public void changeName_EmptyNewNameTest() {
        errorMessage = "Название магазина не может быть пустым";
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            storeService.changeName("store", "");
        });

        assertEquals(errorMessage, exception.getMessage(), currentErrorMessage);

        verify(storeRepository, never()).save(any(Store.class));
    }

    @Test
    public void changeName_EmptyOldNameTest() {
        errorMessage = "Название магазина не может быть пустым";
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            storeService.changeName("", "store");
        });

        assertEquals(errorMessage, exception.getMessage(), currentErrorMessage);

        verify(storeRepository, never()).save(any(Store.class));
    }

    @Test
    public void changeName_NotFoundUserTest() {
        errorMessage = "Пользователь не найден";
        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            storeService.changeName("Тестеровочка", "Тестер");
        });

        assertEquals(errorMessage, exception.getMessage(), currentErrorMessage);

        verify(storeRepository, never()).save(any(Store.class));
    }

    @Test
    public void changeName_NotFoundStoreTest() {
        errorMessage = "Магазин с названием 'Test Store' не найден";
        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(storeRepository.findByName(storeName)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            storeService.changeName(storeName, "тести");
        });

        assertEquals(errorMessage, exception.getMessage(), currentErrorMessage);

        verify(storeRepository, never()).save(any(Store.class));
    }

    @Test
    public void changeName_StoreNameIsExistsTest() {
        errorMessage = "Магазин с названием 'Exists' существует";
        String existsName = "Exists";

        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(storeRepository.findByName(storeName)).thenReturn(Optional.of(testStore));
        when(storeRepository.existsByName(existsName)).thenReturn(true);

        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class, () -> {
            storeService.changeName(storeName, existsName);
        });

        assertEquals(errorMessage, exception.getMessage(), currentErrorMessage);

        verify(storeRepository, never()).save(any(Store.class));
    }

    @Test
    public void changeName_NewNameEqualsOldNameTest() {
        errorMessage = "Название не может совпадать";
        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(storeRepository.findByName(storeName)).thenReturn(Optional.of(testStore));

        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            storeService.changeName(storeName, storeName);
        });

        assertEquals(errorMessage, exception.getMessage(), currentErrorMessage);

        verify(storeRepository, never()).save(any(Store.class));
    }

    @Test
    public void changeName_AccessDeniedStoreTest() {
        errorMessage = "Пользователю не принадлежит магазин";
        Store accessDeniedStore = new Store();
        String anotherTestStoreName = "Access Denied";
        accessDeniedStore.setName(anotherTestStoreName);
        Account anotherUser = new Account();
        accessDeniedStore.setOwner(anotherUser);

        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(storeRepository.findByName(anotherTestStoreName)).thenReturn(Optional.of(accessDeniedStore));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            storeService.changeName(anotherTestStoreName, "Самый новый");
        });

        assertEquals(errorMessage, exception.getMessage(), currentErrorMessage);

        verify(storeRepository, never()).save(any(Store.class));
    }

    @Test
    public void changeName_NameSymbolMoreLimitTest() {
        errorMessage = "Название магазина должно быть от 3 до 30 символов";
        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(storeRepository.findByName(storeName)).thenReturn(Optional.of(testStore));

        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            storeService.changeName(storeName, "12");
        });

        assertEquals(errorMessage, exception.getMessage(), currentErrorMessage);

        verify(storeRepository, never()).save(any(Store.class));
    }

    @Test
    public void changeName_NameSymbolLessLimitTest() {
        errorMessage = "Название магазина должно быть от 3 до 30 символов";
        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(storeRepository.findByName(storeName)).thenReturn(Optional.of(testStore));

        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            storeService.changeName(storeName, "123456789_123456789_123456789_1");
        });

        assertEquals(errorMessage, exception.getMessage(), currentErrorMessage);

        verify(storeRepository, never()).save(any(Store.class));
    }

    @Test
    public void changeName_SuccessTest() {
        String newNameStore = "Самый новый";

        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(storeRepository.findByName(storeName)).thenReturn(Optional.of(testStore));

        StoreOperationResult result = storeService.changeName(storeName, newNameStore);

        assertNotNull(result);

        assertEquals(StoreOperationType.CHANGE_STORENAME, result.getOperationName(),
                "Значение должно быть 'Смена названия'");
        assertEquals(newNameStore, result.getStoreName(), "Значение должно быть 'Самый новый'");
        assertEquals(result.getOwnerName(), userName, "Значение должно быть 'testUser'");

        verify(storeRepository, times(1)).save(any(Store.class));
    }
}