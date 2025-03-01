package app.unit.controller;

import app.controller.InventoryController;
import app.repository.InventoryRepository;
import app.repository.StoreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InventoryControllerTest {

    @Mock
    private StoreRepository storeRepository;
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private Model model;

    @InjectMocks
    private InventoryController inventoryController;

    @Test
    public void testGetAllProductsForm() {
        when(storeRepository.findAll()).thenReturn(Collections.emptyList());

        String viewName = inventoryController.getAllProductsForm(model);

        assertEquals("getAllProductsForm", viewName);

        verify(model).addAttribute("stores", Collections.emptyList());
        verify(model).addAttribute("products", Collections.emptyList());
    }
}