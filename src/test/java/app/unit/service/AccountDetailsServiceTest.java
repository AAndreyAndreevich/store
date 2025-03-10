package app.unit.service;

import app.entity.Account;
import app.entity.Role;
import app.repository.AccountRepository;
import app.repository.RoleRepository;
import app.service.AccountDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class AccountDetailsServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private AccountDetailsService accountDetailsService;

    @Test
    public void testRegisterUser_Success() {
        Account user = new Account();
        user.setUsername("testUser");
        user.setPassword("password");

        Role userRole = new Role();
        userRole.setName("ROLE_USER");

        when(roleRepository.findByName("ROLE_USER")).thenReturn(userRole);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        accountDetailsService.registerUser(user);

        assertEquals(Collections.singleton(userRole), user.getRoles(), "Значение должно быть 'testUser'");
        assertEquals("encodedPassword", user.getPassword(), "Значение должно быть 'password'");
        assertEquals(new BigDecimal(5000), user.getBalance(), "Значение должно быть '5000'");
        assertTrue(user.isActive());
        verify(accountRepository, times(1)).save(user);
    }

    @Test
    public void testLoadUserByUsername_Success() {
        String username = "testUser";
        Account account = new Account();
        account.setId(1L);
        account.setUsername(username);
        account.setPassword("encodedPassword");
        account.setActive(true);

        Role userRole = new Role();
        userRole.setName("ROLE_USER");
        account.setRoles(Collections.singleton(userRole));

        when(accountRepository.findByUsername(username)).thenReturn(Optional.of(account));

        UserDetails userDetails = accountDetailsService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername(), "Значение должно быть 'testUser'");
        assertEquals("encodedPassword", userDetails.getPassword(), "Значение должно быть 'password'");
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority ->
                        grantedAuthority.getAuthority().equals("ROLE_USER")));

        verify(accountRepository, times(1)).findByUsername(username);
    }

    @Test
    public void testLoadUsername_UserNotFound() {
        String username = "nonExistentUser";
        when(accountRepository.findByUsername(username)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            accountDetailsService.loadUserByUsername(username);
        });

        assertEquals("Пользователь с таким именем не найден: testUser", exception.getMessage(),
                "Сообщение должно быть 'Пользователь с таким именем не найден: testUser'");
        verify(accountRepository, times(1)).findByUsername(username);
    }
}