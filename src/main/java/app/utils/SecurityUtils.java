package app.utils;

import app.dto.AccountDetails;
import app.handler.NotFoundException;
import app.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    @Autowired
    public SecurityUtils() {}

    public Long getCurrentUserId(AccountRepository accountRepository) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new RuntimeException("Пользователь не авторизирован");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof AccountDetails) {
            return ((AccountDetails) principal).getId();
        } else if (principal instanceof String) {
            String username = (String) principal;
            return accountRepository.findByUsername(username)
                    .orElseThrow(() -> new NotFoundException("Пользователь не найден"))
                    .getId();
        } else if (principal instanceof Long) {
            return (Long) principal;
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }
}