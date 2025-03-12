package app.integration.controller;

import app.dto.InventoryOperationResult;
import app.enam.InventoryOperationType;
import app.handler.InsufficientBalanceException;
import app.handler.InvalidInputException;
import app.handler.NotFoundException;
import app.repository.StoreRepository;
import app.service.InventoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
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

    private final String manageProductUrl = "/inv/manageProduct";

    private final InventoryOperationType buyOperation = InventoryOperationType.BUY_PRODUCT;
    private final InventoryOperationType sellOperation = InventoryOperationType.SELL_PRODUCT;

    @Test
    public void testGetAllProductsFormTest() throws Exception {
        when(storeRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/inv/getAllProductsForm"))
                .andExpect(status().isOk())
                .andExpect(view().name("getAllProductsForm"))
                .andExpect(model().attribute("stores", Collections.emptyList()))
                .andExpect(model().attribute("products", Collections.emptyList()));
    }

    @Test
    public void testGetAllProducts_NotFound() throws Exception {
        when(inventoryService.getAllProducts(storeId)).thenThrow(
                new NotFoundException("Магазин пуст или его не существует"));

        mockMvc.perform(get("/inv/getAllProduct")
                .param("storeId", storeId.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Магазин пуст или его не существует"));
    }

    @Test
    public void testManageProduct_BuyProduct() throws Exception {
        InventoryOperationResult result = new InventoryOperationResult(
                buyOperation,
                BigDecimal.valueOf(1000),
                "product",
                count,
                "user",
                "store",
                true
        );

        when(inventoryService.manageProduct(storeId, productId, count, buyOperation)).thenReturn(result);

        mockMvc.perform(post("/inv/manageProduct")
                .param("storeId", storeId.toString())
                .param("productId", productId.toString())
                .param("count", count.toString())
                .param("operationType", buyOperation.name()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)));
    }

    @Test
    public void testManageProduct_SellProduct() throws Exception {
        InventoryOperationResult result = new InventoryOperationResult(
                buyOperation,
                BigDecimal.valueOf(1000),
                "product",
                count,
                "user",
                "store",
                true
        );

        when(inventoryService.manageProduct(storeId, productId, count, sellOperation)).thenReturn(result);

        mockMvc.perform(post("/inv/manageProduct")
                        .param("storeId", storeId.toString())
                        .param("productId", productId.toString())
                        .param("count", count.toString())
                        .param("operationType", buyOperation.name()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)));
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

    @Test
    public void testManageProduct_InsufficientBalance() throws Exception {
        when(inventoryService.manageProduct(storeId, productId, count, buyOperation))
                .thenThrow(new InsufficientBalanceException("Недостаточно средств на балансе для покупки"));

        mockMvc.perform(post(manageProductUrl)
                        .param("storeId", storeId.toString())
                        .param("productId", productId.toString())
                        .param("count", count.toString())
                        .param("operationType", buyOperation.name()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Недостаточно средств на балансе для покупки"));
    }
}