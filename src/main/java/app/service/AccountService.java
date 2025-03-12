package app.service;

import app.dto.AccountOperationResult;
import app.enam.AccountOperationType;
import app.entity.Account;
import app.handler.*;
import app.repository.AccountRepository;
import app.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

@Service
public class AccountService {

    private static final Integer MIN_USERNAME_LENGTH = 4;
    private static final Integer MAX_USERNAME_LENGTH = 20;
    private static final Integer MIN_PASSWORD_LENGTH = 6;
    private static final Integer MAX_PASSWORD_LENGTH = 30;

    private final AccountDetailsService accountDetailsService;
    private final AccountRepository accountRepository;
    private final SecurityUtils securityUtils;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AccountService(
            AccountDetailsService accountDetailsService,
            AccountRepository accountRepository,
            SecurityUtils securityUtils,
            PasswordEncoder passwordEncoder) {
        this.accountDetailsService = accountDetailsService;
        this.accountRepository = accountRepository;
        this.securityUtils = securityUtils;
        this.passwordEncoder = passwordEncoder;
    }

    public AccountOperationResult registration(String username, String password) {
        if (accountRepository.findByUsername(username).isPresent()) {
            throw new InvalidUsernameException("Пользователь с таким именем уже существует: " + username);
        }

        checkUsernameLength(username);
        checkPasswordLength(password);

        Account account = new Account();
        account.setUsername(username);
        account.setPassword(password);
        accountDetailsService.registrationUser(account);
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
    public AccountOperationResult changeName(String oldName, String newName) {
        if (StringUtils.isEmpty(newName) || StringUtils.isEmpty(oldName)) {
            throw new InvalidInputException("Имя пользователя не может быть пустым");
        }
        checkUsernameLength(newName);
        Long currentId = securityUtils.getCurrentUserId(accountRepository);
        Account account = accountRepository.findByUsername(oldName)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (!currentId.equals(account.getId())) {
            throw new AccessDeniedException("Пользователю не принадлежит это имя");
        }
        if (accountRepository.existsByUsername(newName)) {
            throw new InvalidInputException("Имя пользователя уже занято");
        }
        account.setUsername(newName);
        accountRepository.save(account);

        return new AccountOperationResult(
                oldName + " -> " + newName, AccountOperationType.CHANGE_USERNAME, true
        );
    }

    @Transactional
    public AccountOperationResult changePassword(String oldPassword, String newPassword) {
        if (StringUtils.isEmpty(newPassword) || StringUtils.isEmpty(oldPassword)) {
            throw new InvalidInputException("Пароль не может быть пустым");
        }
        checkPasswordLength(newPassword);
        Long currentId = securityUtils.getCurrentUserId(accountRepository);
        Account account = accountRepository.findById(currentId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (!passwordEncoder.matches(oldPassword, account.getPassword())) {
            throw new InvalidPasswordException("Неправильно указан действующий пароль");
        }
        if (passwordEncoder.matches(newPassword, account.getPassword())) {
            throw new InvalidPasswordException("Новый пароль должен отличаться от старого");
        }

        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);

        return new AccountOperationResult(
                account.getUsername(), AccountOperationType.CHANGE_PASSWORD, true
        );
    }

    private void checkPasswordLength(String password) {
        if (password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH) {
            throw new InvalidPasswordException("Пароль должен быть от " + MIN_PASSWORD_LENGTH + " до " +
                    MAX_PASSWORD_LENGTH + " символов");
        }
    }

    private void checkUsernameLength(String username) {
        if (username.length() < MIN_USERNAME_LENGTH || username.length() > MAX_USERNAME_LENGTH) {
            throw new InvalidUsernameException("Имя пользователя должно быть от " + MIN_USERNAME_LENGTH + " до " +
                    MAX_USERNAME_LENGTH + " символов");
        }
    }

//    на будущее оставлю, пока не использую
//    private void checkPasswordComplexity(String password) {
//        if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
//            throw new InvalidPasswordException("Пароль должен содержать цифры, специальные символы, заглавные и строчные буквы");
//        }
//    }
}