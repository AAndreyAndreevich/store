package app.service;

import app.dto.StoreOperationResult;
import app.enam.StoreOperationType;
import app.entity.Account;
import app.entity.Store;
import app.repository.AccountRepository;
import app.repository.StoreRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
    void setUp() {
        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setUsername("testUser");

        storeName = "Test Store";
    }

    @Test
    void createStore_Success() {
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
}