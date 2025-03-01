package app.integration.controller;

import app.dto.StoreOperationResult;
import app.enam.StoreOperationType;
import app.entity.Account;
import app.handler.AlreadyExistsException;
import app.handler.InvalidInputException;
import app.handler.NotFoundException;
import app.service.StoreService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final String storeName = "testStore";
    private final String createStoreUrl = "/store/createStore";

    private final StoreOperationType createStoreOperation = StoreOperationType.CREATE;

    @Test
    public void testCreateStoreForm() throws Exception {
        mockMvc.perform(get("/store/createStoreForm"))
                .andExpect(status().isOk())
                .andExpect(view().name("createStoreForm"));
    }

    @Test
    public void testCreateStore_Success() throws Exception {
        Account testUser = new Account();
        testUser.setId(1L);
        testUser.setUsername("testUser");

        StoreOperationResult result = new StoreOperationResult(
                createStoreOperation, testUser.getUsername(), storeName
        );

        when(storeService.createStore(storeName)).thenReturn(result);

        mockMvc.perform(post(createStoreUrl)
                .param("storeName", storeName))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)));
    }

    @Test
    public void testCreateStore_UserNotFound() throws Exception {
        when(storeService.createStore(storeName))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mockMvc.perform(post(createStoreUrl)
                .param("storeName", storeName))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Пользователь не найден"));
    }

    @Test
    public void testCreateStore_ExistsName() throws Exception {
        when(storeService.createStore(storeName))
                .thenThrow(new AlreadyExistsException("Магазин с названием '" + storeName + "' существует"));

        mockMvc.perform(post(createStoreUrl)
                .param("storeName", storeName))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Магазин с названием '" + storeName + "' существует"));
    }

    @Test
    public void testCreateStore_EmptyName() throws Exception {
        when(storeService.createStore(""))
                .thenThrow(new InvalidInputException("Название магазина не может быть пустым"));

        mockMvc.perform(post(createStoreUrl)
                .param("storeName", ""))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Название магазина не может быть пустым"));
    }
}