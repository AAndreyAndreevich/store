package app.controller;

import app.dto.AccountOperationResult;
import app.entity.Account;
import app.handler.*;
import app.service.AccountService;
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
    public ResponseEntity<?> registrationUser(@RequestParam String username, @RequestParam String password) {
        try {
            AccountOperationResult result = accountService.registration(username, password);
            return ResponseEntity.ok(result);
        } catch (InvalidUsernameException | InvalidPasswordException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Внутренняя ошибка сервера: " + e.getMessage());
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
            return ResponseEntity.internalServerError().body("Внутренняя ошибка сервера: " + e.getMessage());
        }
    }

    @GetMapping("/changeName")
    public String changeUsernameForm(Model model) {
        model.addAttribute("account", new Account());
        return "changeUsername";
    }

    @PostMapping("/changeName")
    public ResponseEntity<?> changeUsername(@RequestParam String oldName, @RequestParam String newName) {
        try {
            AccountOperationResult result = accountService.changeName(oldName, newName);
            return ResponseEntity.ok(result);
        } catch (InvalidInputException | InvalidUsernameException | AccessDeniedException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Внутренняя ошибка сервера: " + e.getMessage());
        }
    }

    @GetMapping("/changePassword")
    public String changePasswordForm(Model model) {
        model.addAttribute("account", new Account());
        return "changePassword";
    }

    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestParam String oldPassword, @RequestParam String newPassword) {
        try {
            AccountOperationResult result = accountService.changePassword(oldPassword, newPassword);
            return ResponseEntity.ok(result);
        } catch (InvalidInputException | InvalidPasswordException | AccessDeniedException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Внутренняя ошибка сервера: " + e.getMessage());
        }
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login?logout";
    }
}