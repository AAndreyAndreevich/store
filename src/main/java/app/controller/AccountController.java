package app.controller;

import app.dto.AccountOperationResult;
import app.entity.Account;
import app.handler.InvalidAuthorizationException;
import app.handler.InvalidPasswordException;
import app.handler.InvalidUsernameException;
import app.service.AccountService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("account", new Account());
        return "registration";
    }

    @PostMapping("/registration")
    public ResponseEntity<?> registerUser(@RequestParam String username, @RequestParam String password) {
        try {
            AccountOperationResult result = accountService.register(username, password);
            return ResponseEntity.ok(result);
        } catch (InvalidUsernameException | InvalidPasswordException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Внутренняя ошибка сервера: " + e.getMessage());
        }
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("account", new Account());
        return "login";
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestParam String username, @RequestParam String password) {
        try {
            AccountOperationResult result = accountService.login(username, password);
            return ResponseEntity.ok(result);
        } catch (InvalidAuthorizationException | UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Внутренняя ошибка сервера: " + e.getMessage());
        }
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login?logout";
    }
}