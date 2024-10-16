package com.gf.yummify.presentation.controller;

import com.gf.yummify.business.services.UserService;
import com.gf.yummify.data.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {
    private UserService userService;
    private UserDetailsService userDetailsService;


    public UserController(UserService userService, UserDetailsService userDetailsService) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/hola")
    public String hola () {
        return "hola";
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
public String login(){
        return "login";
}
    @PostMapping("/login")
    public String loginValidation(Model model,
                                  @RequestParam("username") String username,
                                  @RequestParam("password") String password) {
        try {
            userDetailsService.loadUserByUsername(username);
            return "login";
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "login";
        }
    }
    @GetMapping("/register")
    public String register() {
        return "register";
    }

    /*@PostMapping("/login")
    public String loginValidation(Model model,
                                  @RequestParam("username") String username,
                                  @RequestParam("password") String password) {
        try {
            userDetailsService.loadUserByUsername(username);
            return "login";
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "login";
        }
    }*/
}
