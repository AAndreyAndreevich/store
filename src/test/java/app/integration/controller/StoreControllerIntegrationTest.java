package app.integration.controller;

import app.dto.StoreOperationResult;
import app.enam.StoreOperationType;
import app.entity.Account;
import app.entity.Store;
import app.handler.AlreadyExistsException;
import app.handler.InvalidInputException;
import app.handler.NotFoundException;
import app.service.StoreService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class StoreControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private StoreService storeService;
    @Autowired
    private ObjectMapper objectMapper;

    private String username;
    private String storeName;
    private String newStoreName;
    private Account account;
    private Store store;

    private final String createStoreUrl = "/store/createStore";
    private final String changeStoreNameUrl = "/store/changeStoreName";

    @BeforeEach
    public void setUp() {
        username = "testUser";
        storeName = "test store";
        newStoreName = "new store name";

        account = new Account();
        account.setUsername(username);
        account.setId(1L);

        store = new Store();
        store.setOwner(account);
        store.setId(1L);
    }

    private final StoreOperationType createStoreOperation = StoreOperationType.CREATE;

    @Test
    public void testChangeStoreNameForm() throws Exception {
        mockMvc.perform(get("/store/changeStoreNameForm"))
                .andExpect(status().isOk())
                .andExpect(view().name("changeStoreName"));
    }

    @Test
    public void testChangeStoreName_Success() throws Exception {
        StoreOperationResult result = new StoreOperationResult(
                createStoreOperation, account.getUsername(), newStoreName
        );

        when(storeService.changeName(storeName, newStoreName)).thenReturn(result);

        mockMvc.perform(post(changeStoreNameUrl)
                        .param("oldName", storeName)
                        .param("newName", newStoreName))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)));
    }

    @Test
    public void testChangeStoreName_InvalidInput() throws Exception {
        String errorMessage = "Название магазина не может быть пустым";

        when(storeService.changeName("", ""))
                .thenThrow(new InvalidInputException(errorMessage));

        mockMvc.perform(post(changeStoreNameUrl)
                .param("oldName", "")
                .param("newName", ""))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorMessage));
    }

    @Test
    public void testChangeStoreName_NotFound() throws Exception {
        String errorMessage = "Пользователь не найден";

        when(storeService.changeName(storeName, newStoreName))
                .thenThrow(new NotFoundException(errorMessage));

        mockMvc.perform(post(changeStoreNameUrl)
                .param("oldName", storeName)
                .param("newName", newStoreName))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorMessage));
    }

    @Test
    public void testChangeStoreName_AlreadyExists() throws Exception {
        String errorMessage = "Магазин с названием '" + storeName + "' существует";

        when(storeService.changeName(storeName, newStoreName)).thenThrow(
                new AlreadyExistsException(errorMessage)
        );

        mockMvc.perform(post(changeStoreNameUrl)
                .param("oldName", storeName)
                .param("newName", newStoreName))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorMessage));
    }

    @Test
    public void testChangeStoreName_InvalidInputLength() throws Exception {
        String errorMessage = "Название магазина должно быть от 3 до 30 символов";
        String longName = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

        when(storeService.changeName(storeName, longName)).thenThrow(new InvalidInputException(errorMessage));

        mockMvc.perform(post(changeStoreNameUrl)
                .param("oldName", storeName)
                .param("newName", longName))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorMessage));
    }

    @Test
    public void testCreateStoreForm() throws Exception {
        mockMvc.perform(get("/store/createStoreForm"))
                .andExpect(status().isOk())
                .andExpect(view().name("createStoreForm"));
    }

    @Test
    public void testCreateStore_Success() throws Exception {
        StoreOperationResult result = new StoreOperationResult(
                createStoreOperation, account.getUsername(), storeName
        );

        when(storeService.createStore(storeName)).thenReturn(result);

        mockMvc.perform(post(createStoreUrl)
                .param("storeName", storeName))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)));
    }

    @Test
    public void testCreateStore_UserNotFound() throws Exception {
        String errorMessage = "Пользователь не найден";

        when(storeService.createStore(storeName))
                .thenThrow(new NotFoundException(errorMessage));

        mockMvc.perform(post(createStoreUrl)
                .param("storeName", storeName))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorMessage));
    }

    @Test
    public void testCreateStore_ExistsName() throws Exception {
        String errorMessage = "Магазин с названием '" + storeName + "' существует";

        when(storeService.createStore(storeName))
                .thenThrow(new AlreadyExistsException(errorMessage));

        mockMvc.perform(post(createStoreUrl)
                .param("storeName", storeName))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorMessage));
    }

    @Test
    public void testCreateStore_EmptyName() throws Exception {
        String errorMessage = "Название магазина не может быть пустым";

        when(storeService.createStore(""))
                .thenThrow(new InvalidInputException(errorMessage));

        mockMvc.perform(post(createStoreUrl)
                .param("storeName", ""))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorMessage));
    }
}