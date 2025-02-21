package app.controller;

import app.entity.Account;
import app.service.AccountDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AccountController {

    private final AccountDetailsService accountService;

    public AccountController(AccountDetailsService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("account", new Account());
        return "registration";
    }

    @PostMapping("/registration")
    public String registerUser(Account account) {
        accountService.registerUser(account);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login?logout";
    }
}