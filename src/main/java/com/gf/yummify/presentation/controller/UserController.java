package com.gf.yummify.presentation.controller;

import com.gf.yummify.business.services.UserService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {
    private UserService userService;
    private UserDetailsService userDetailsService;


    public UserController(UserService userService, UserDetailsService userDetailsService) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/admin/panel")
    public String administratorPanel() {
        return "administratorPanel";
    }

    @PostMapping("/user")
    public String registerUser(@RequestParam("email") String email,
                               @RequestParam("username") String username,
                               @RequestParam("password") String password,
                               RedirectAttributes redirectAttributes) {
        userService.createUser(username, email, password);
        redirectAttributes.addFlashAttribute("success", "Usuario registrado correctamente");
        return "redirect:/register"; // redirige de vuelta al formulario de registro
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

}
