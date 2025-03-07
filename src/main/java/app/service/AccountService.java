package app.service;

import app.dto.AccountOperationResult;
import app.enam.AccountOperationType;
import app.entity.Account;
import app.handler.InvalidAuthorizationException;
import app.handler.InvalidPasswordException;
import app.handler.InvalidUsernameException;
import app.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private static final int MIN_USERNAME_LENGTH = 4;
    private static final int MAX_USERNAME_LENGTH = 20;
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 30;

    private final AccountDetailsService accountDetailsService;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AccountService(
            AccountDetailsService accountDetailsService,
            AccountRepository accountRepository,
            PasswordEncoder passwordEncoder) {
        this.accountDetailsService = accountDetailsService;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AccountOperationResult register(String username, String password) {
        if (accountRepository.findByUsername(username).isPresent()) {
            throw new InvalidUsernameException("Пользователь с таким именем уже существует: " + username);
        }

        if (username.length() < MIN_USERNAME_LENGTH || username.length() > MAX_USERNAME_LENGTH) {
            throw new InvalidUsernameException("Имя пользователя должно быть от " + MIN_USERNAME_LENGTH + " до " +
                    MAX_USERNAME_LENGTH + " символов");
        }

        if (password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH) {
            throw new InvalidPasswordException("Пароль должен быть от " + MIN_PASSWORD_LENGTH + " до " +
                    MAX_PASSWORD_LENGTH + " символов");
        }

        Account account = new Account();
        account.setUsername(username);
        account.setPassword(password);
        accountDetailsService.registerUser(account);
        return new AccountOperationResult(username, AccountOperationType.REGISTRATION_ACCOUNT, true);
    }

    public AccountOperationResult login(String username, String password) {
        if (accountRepository.findByUsername(username).isEmpty()) {
            throw new InvalidAuthorizationException("Пользователя с данным именем не существует: " + username);
        }
        if (!passwordEncoder.matches(password, accountRepository.findByUsername(username).get().getPassword())) {
            throw new InvalidAuthorizationException("Пароль указан не верно");
        }
        accountDetailsService.loadUserByUsername(username);
        return new AccountOperationResult(username, AccountOperationType.LOG_IN, true);
    }

    @Transactional
    public AccountOperationResult changeName(String name) {
        //смена имени для пользователя
        return null;
    }

    @Transactional
    public AccountOperationResult changePassword(String oldPassword, String newPassword) {
        //смена пароля для пользователя
        return null;
    }
}