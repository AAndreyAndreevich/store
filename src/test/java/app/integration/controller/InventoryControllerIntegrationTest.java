package app.integration.controller;

import app.StoreApplication;
import app.enam.InventoryOperationType;
import app.handler.InvalidInputException;
import app.handler.NotFoundException;
import app.integration.configuration.TestSecurityConfig;
import app.repository.StoreRepository;
import app.service.InventoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class InventoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private InventoryService inventoryService;
    @MockitoBean
    private StoreRepository storeRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private final Long storeId = 1L;
    private final Long productId = 1L;
    private final Integer count = 10;

    private final String getAllProductUrl = "/inv/getAllProduct";
    private final String manageProductUrl = "/inv/manageProduct";

    private final InventoryOperationType buyOperation = InventoryOperationType.BUY_PRODUCT;
    private final InventoryOperationType sellOperation = InventoryOperationType.SELL_PRODUCT;

    @Test
    public void getAllProductsFormTest() throws Exception {
        when(storeRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/inv/getAllProductsForm"))
                .andExpect(status().isOk())
                .andExpect(view().name("getAllProductsForm"))
                .andExpect(model().attribute("stores", Collections.emptyList()))
                .andExpect(model().attribute("products", Collections.emptyList()));
    }

    @Test
    public void getAllProducts_NotFound() throws Exception {
        when(inventoryService.getAllProducts(storeId)).thenThrow(
                new NotFoundException("Магазин пуст или его не существует"));

        mockMvc.perform(get(getAllProductUrl)
                .param("storeId", storeId.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Магазин пуст или его не существует"));
    }

    @Test
    public void testManageProduct_InvalidInput() throws Exception {
        when(inventoryService.manageProduct(storeId, productId, count, buyOperation))
                .thenThrow(new InvalidInputException("Количество не может быть равно или меньше нуля"));

        mockMvc.perform(post(manageProductUrl)
                .param("storeId", storeId.toString())
                .param("productId", productId.toString())
                .param("count", count.toString())
                .param("operationType", buyOperation.name()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Количество не может быть равно или меньше нуля"));
    }
}