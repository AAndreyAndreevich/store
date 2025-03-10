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

    @BeforeEach
    public void setUp() {
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
        String emptyStoreName = "";

        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            storeService.createStore(emptyStoreName);
        });

        assertEquals("Название магазина не может быть пустым", exception.getMessage(),
                "Сообщение должно быть 'Название магазина не может быть пустым'");
        verify(storeRepository, never()).save(any(Store.class));
    }

    @Test
    public void createStore_StoreAlreadyExistsTest() {
        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(storeRepository.existsByName(storeName)).thenReturn(true);

        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class, () -> {
            storeService.createStore(storeName);
        });

        assertEquals("Магазин с названием 'Test Store' существует", exception.getMessage(),
                "Сообщение должно быть 'Магазин с названием 'Test Store' существует'");

        verify(storeRepository, never()).save(any(Store.class));
    }

    @Test
    public void createStore_UserNotFountTest() {
        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
           storeService.createStore(storeName);
        });

        assertEquals("Пользователь не найден", exception.getMessage(),
                "Сообщение должно быть 'Пользователь не найден'");

        verify(storeRepository, never()).save(any(Store.class));
    }

    @Test
    public void changeName_EmptyNewNameTest() {
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            storeService.changeName("store", "");
        });

        assertEquals("Название магазина не может быть пустым", exception.getMessage(),
                "Сообщение должно быть 'Название магазина не может быть пустым'");

        verify(storeRepository, never()).save(any(Store.class));
    }

    @Test
    public void changeName_EmptyOldNameTest() {
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            storeService.changeName("", "store");
        });

        assertEquals("Название магазина не может быть пустым", exception.getMessage(),
                "Сообщение должно быть 'Название магазина не может быть пустым'");

        verify(storeRepository, never()).save(any(Store.class));
    }

    @Test
    public void changeName_NotFoundUserTest() {
        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            storeService.changeName("Тестеровочка", "Тестер");
        });

        assertEquals("Пользователь не найден", exception.getMessage(),
                "Сообщение должно быть 'Пользователь не найден'");

        verify(storeRepository, never()).save(any(Store.class));
    }

    @Test
    public void changeName_NotFoundStoreTest() {
        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(storeRepository.findByName(storeName)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            storeService.changeName(storeName, "тести");
        });

        assertEquals("Магазин с названием 'Test Store' не найден", exception.getMessage(),
                "Сообщение должно быть 'Магазин с названием 'Test Store' не найден'");

        verify(storeRepository, never()).save(any(Store.class));
    }

    @Test
    public void changeName_StoreNameIsExistsTest() {
        String existsName = "Exists";

        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(storeRepository.findByName(storeName)).thenReturn(Optional.of(testStore));
        when(storeRepository.existsByName(existsName)).thenReturn(true);

        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class, () -> {
            storeService.changeName(storeName, existsName);
        });

        assertEquals("Магазин с названием 'Exists' существует", exception.getMessage(),
                "Сообщение должно быть 'Магазин с названием 'Exists' существует'");

        verify(storeRepository, never()).save(any(Store.class));
    }

    @Test
    public void changeName_NewNameEqualsOldNameTest() {
        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(storeRepository.findByName(storeName)).thenReturn(Optional.of(testStore));

        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            storeService.changeName(storeName, storeName);
        });

        assertEquals("Название не может совпадать", exception.getMessage(),
                "Сообщение должно быть 'Название не может совпадать'");

        verify(storeRepository, never()).save(any(Store.class));
    }

    @Test
    public void changeName_AccessDeniedStoreTest() {
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

        assertEquals("Пользователю не принадлежит магазин", exception.getMessage(),
                "Сообщение должно быть 'Пользователю не принадлежит магазин'");

        verify(storeRepository, never()).save(any(Store.class));
    }

    @Test
    public void changeName_NameSymbolMoreLimitTest() {
        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(storeRepository.findByName(storeName)).thenReturn(Optional.of(testStore));

        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            storeService.changeName(storeName, "12");
        });

        assertEquals("Название магазина должно быть от 3 до 30 символов", exception.getMessage(),
                "Сообщение должно быть 'Название магазина должно быть от 3 до 30 символов'");

        verify(storeRepository, never()).save(any(Store.class));
    }

    @Test
    public void changeName_NameSymbolLessLimitTest() {
        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(storeRepository.findByName(storeName)).thenReturn(Optional.of(testStore));

        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            storeService.changeName(storeName, "123456789_123456789_123456789_1");
        });

        assertEquals("Название магазина должно быть от 3 до 30 символов", exception.getMessage(),
                "Сообщение должно быть 'Название магазина должно быть от 3 до 30 символов'");

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