package app.unit.service;

import app.dto.StoreOperationResult;
import app.enam.StoreOperationType;
import app.entity.Account;
import app.entity.Store;
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
    private String storeName;

    @BeforeEach
    public void setUp() {
        storeName = "Test Store";

        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setUsername("testUser");
    }

    @Test
    public void testCreateStore_Success() {
        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(storeRepository.existsByName(storeName)).thenReturn(false);

        StoreOperationResult result = storeService.createStore(storeName);

        assertNotNull(result);
        assertEquals(StoreOperationType.CREATE, result.getOperationName());
        assertEquals(testAccount.getUsername(), result.getOwnerName());
        assertEquals(storeName, result.getStoreName());

        verify(storeRepository, times(1)).save(any(Store.class));
    }

    @Test
    public void testCreateStore_EmptyStoreName() {
        String emptyStoreName = "";

        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            storeService.createStore(emptyStoreName);
        });

        assertEquals("Название магазина не может быть пустым", exception.getMessage());
        verify(storeRepository, never()).save(any(Store.class));
    }

    @Test
    public void testCreateStore_StoreAlreadyExists() {
        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(storeRepository.existsByName(storeName)).thenReturn(true);

        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class, () -> {
            storeService.createStore(storeName);
        });

        assertEquals("Магазин с названием '" + storeName + "' существует", exception.getMessage());

        verify(storeRepository, never()).save(any(Store.class));
    }

    @Test
    public void testCreateStore_UserNotFount() {
        when(securityUtils.getCurrentUserId(accountRepository)).thenReturn(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
           storeService.createStore(storeName);
        });

        assertEquals("Пользователь не найден", exception.getMessage());

        verify(storeRepository, never()).save(any(Store.class));
    }
}