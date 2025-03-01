package app.integration.controller;

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

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Test
    public void getAllProductsFormTest() throws Exception {
        when(storeRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/inv/getAllProductsForm"))
                .andExpect(status().isOk())
                .andExpect(content().string("getAllProductsForm"));
    }
}