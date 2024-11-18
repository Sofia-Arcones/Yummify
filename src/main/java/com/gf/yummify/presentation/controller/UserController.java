package com.gf.yummify.presentation.controller;

import com.gf.yummify.business.services.UserService;
import com.gf.yummify.data.entity.User;
import com.gf.yummify.presentation.dto.RegisterDTO;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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

    @PostMapping("/register")
    public String registerUser(@Valid RegisterDTO registerDTO,
                               Model model) {

        try {
            userService.createUser(registerDTO);
            model.addAttribute("success", "Usuario registrado correctamente");
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("user", registerDTO);
        }

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

            if (authentication != null && authentication.isAuthenticated()) {
                model.addAttribute("isOwnProfile", userService.checkUserAuthentication(authentication.getName(), profileUser.getUsername()));
            } else {
                model.addAttribute("isOwnProfile", false);
            }
            return "users/profile";
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "error";
        }
    }

}
