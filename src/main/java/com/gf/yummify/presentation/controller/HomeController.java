package com.gf.yummify.presentation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home() {
        return "index";  // Asegúrate de que existe un archivo index.html o index.html en templates.
    }
}
