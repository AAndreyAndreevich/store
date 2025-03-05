package app.service;

import app.dto.AccountOperationResult;
import app.enam.AccountOperationType;
import app.entity.Account;
import app.handler.InvalidAuthorizationException;
import app.handler.InvalidPasswordException;
import app.handler.InvalidUsernameException;
import app.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

public class AccountService {

    private static final int MIN_USERNAME_LENGTH = 4;
    private static final int MAX_USERNAME_LENGTH = 20;
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 30;

    private AccountDetailsService accountDetailsService;
    private AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountDetailsService accountDetailsService, AccountRepository accountRepository) {
        this.accountDetailsService = accountDetailsService;
        this.accountRepository = accountRepository;
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
        UserDetails userDetails = accountDetailsService.loadUserByUsername(username);
        if (userDetails != null && password.equals(userDetails.getPassword())) {
            return new AccountOperationResult(username, AccountOperationType.LOG_IN, true);
        } else {
            throw new InvalidAuthorizationException("Неверный логин или пароль");
        }
    }

}