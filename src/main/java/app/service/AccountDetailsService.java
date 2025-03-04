package app.service;

import app.dto.AccountDetails;
import app.entity.Account;
import app.entity.Role;
import app.handler.InvalidPasswordException;
import app.handler.InvalidUsernameException;
import app.repository.AccountRepository;
import app.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;

@Service
public class AccountDetailsService  implements UserDetailsService {

    private static final int MIN_USERNAME_LENGTH = 4;
    private static final int MAX_USERNAME_LENGTH = 20;
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 30;

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AccountDetailsService(AccountRepository accountRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerUser(Account user) {
        if (accountRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new InvalidUsernameException("Пользователь с таким именем уже существует: " + user.getUsername());
        }

        if (user.getUsername().length() < MIN_USERNAME_LENGTH || user.getUsername().length() > MAX_USERNAME_LENGTH) {
            throw new InvalidUsernameException("Имя пользователя должно быть от " + MIN_USERNAME_LENGTH + " до " +
                    MAX_USERNAME_LENGTH + " символов");
        }

        if (user.getPassword().length() < MIN_PASSWORD_LENGTH || user.getPassword().length() > MAX_PASSWORD_LENGTH) {
            throw new InvalidPasswordException("Пароль должен быть от " + MIN_PASSWORD_LENGTH + " до " +
                    MAX_PASSWORD_LENGTH + " символов");
        }

        Role userRole = roleRepository.findByName("ROLE_USER");
        user.setRoles(Collections.singleton(userRole));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setBalance(new BigDecimal(5000));
        user.setActive(true);
        accountRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с таким именем не найден: " + username));
        if (!account.isActive()) {
            throw new UsernameNotFoundException("Аккаунт не активен: " + username);
        }
        Long id = account.getId();
        String password = account.getPassword();
        Collection<String> roles = account.getRoles().stream()
                .map(Role::getName)
                .toList();
        boolean active = account.isActive();
        return new AccountDetails(id, username, password, roles, active);
    }
}
