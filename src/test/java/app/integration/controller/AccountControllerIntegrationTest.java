package app.integration.controller;

import app.dto.AccountOperationResult;
import app.enam.AccountOperationType;
import app.handler.InvalidAuthorizationException;
import app.handler.InvalidInputException;
import app.handler.InvalidPasswordException;
import app.handler.InvalidUsernameException;
import app.service.AccountDetailsService;
import app.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("Integration")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private AccountDetailsService accountDetailsService;
    @MockitoBean
    private AccountService accountService;
    @Autowired
    private ObjectMapper objectMapper;

    private String username;
    private String password;

    @BeforeEach
    public void setUp() {
        username = "testUser";
        password = "testPassword";
    }

    @Test
    public void testRegistration_Success() throws Exception {
        AccountOperationResult result =
                new AccountOperationResult(username, AccountOperationType.REGISTRATION_ACCOUNT, true);

        when(accountService.registration(username, password)).thenReturn(result);

        mockMvc.perform(post("/registration")
                .param("username", username)
                .param("password", password)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)));
    }

    @Test
    public void testRegistration_InvalidUsername() throws Exception {
        String errorMessage = "Пользователь с таким именем уже существует: " + username;

        when(accountService.registration(username, password)).thenThrow(new InvalidUsernameException(errorMessage));

        mockMvc.perform(post("/registration")
                .param("username", username)
                .param("password", password)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorMessage));
    }

    @Test
    public void testLogin_Success() throws Exception {
        AccountOperationResult result = new AccountOperationResult(username, AccountOperationType.LOG_IN, true);
        when(accountService.login(username, password)).thenReturn(result);

        mockMvc.perform(post("/login")
                .param("username", username)
                .param("password", password)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)));
    }

    @Test
    public void testLogin_InvalidAuthorization() throws Exception {
        String errorMessage = "Пользователя с данным именем не существует: " + username;

        when(accountService.login(username, password)).thenThrow(new InvalidAuthorizationException(errorMessage));

        mockMvc.perform(post("/login")
                .param("username", username)
                .param("password", password)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorMessage));
    }

    @Test
    public void testChangeUsername_Success() throws Exception {
        String oldName = "oldUser";
        String newName = "newUser";
        AccountOperationResult result = new AccountOperationResult(oldName + " -> " + newName,
                AccountOperationType.CHANGE_USERNAME, true);

        when(accountService.changeName(oldName, newName)).thenReturn(result);

        mockMvc.perform(post("/changeName")
                .param("oldName", oldName)
                .param("newName", newName)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)));
    }

    @Test
    public void testChangeUsername_InvalidInput() throws Exception {
        String oldName = "oldUser";
        String newName = "newUser";
        String errorMessage = "Имя пользователя уже занято";

        when(accountService.changeName(oldName, newName)).thenThrow(new InvalidInputException(errorMessage));

        mockMvc.perform(post("/changeName")
                .param("oldName", oldName)
                .param("newName", newName)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorMessage));
    }

    @Test
    public void testChangePassword_Success() throws Exception {
        String oldPassword = "oldPassword123";
        String newPassword = "newPassword123";
        AccountOperationResult result = new AccountOperationResult(username,
                AccountOperationType.CHANGE_PASSWORD, true);

        when(accountService.changePassword(oldPassword, newPassword)).thenReturn(result);

        mockMvc.perform(post("/changePassword")
                .param("oldPassword", oldPassword)
                .param("newPassword", newPassword)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)));
    }

    @Test
    public void testChangePassword_InvalidPassword() throws Exception {
        String oldPassword = "oldPassword123";
        String newPassword = "oldPassword123";
        String errorMessage = "Новый пароль должен отличаться от старого";

        when(accountService.changePassword(oldPassword, newPassword)).thenThrow(new InvalidPasswordException(errorMessage));

        mockMvc.perform(post("/changePassword")
                        .param("oldPassword", oldPassword)
                        .param("newPassword", newPassword)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorMessage));
    }

    @Test
    public void testLogout() throws Exception {
        mockMvc.perform(get("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout"));
    }
}