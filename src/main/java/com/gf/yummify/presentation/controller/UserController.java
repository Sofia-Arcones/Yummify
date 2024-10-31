package com.gf.yummify.presentation.controller;

import com.gf.yummify.business.services.UserService;
import com.gf.yummify.data.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        return "users/administratorPanel";
    }

    @PostMapping("/user")
    public String registerUser(@RequestParam("email") String email,
                               @RequestParam("username") String username,
                               @RequestParam("password") String password,
                               Model model) {
        userService.createUser(username, email, password);
        model.addAttribute("success", "Usuario registrado correctamente");
        return "users/register";
    }

    @GetMapping("/login")
    public String login() {
        return "users/login";
    }

    @GetMapping("/register")
    public String register() {
        return "users/register";
    }

    @Transactional
    @GetMapping("/users/profile/{username}")
    public String viewProfile(@PathVariable String username, Model model, Authentication authentication) {
        try {
            User profileUser = userService.findUserByUsername(username);
            model.addAttribute("user", profileUser);
            model.addAttribute("recipes", profileUser.getRecipes());
            model.addAttribute("isOwnProfile", userService.checkUserAuthentication(authentication.getName(), profileUser.getUsername()));
            return "users/profile";
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "error";
        }
    }

}
