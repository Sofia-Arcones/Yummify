package com.gf.yummify.presentation.controller;

import com.gf.yummify.business.services.RecipeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private RecipeService recipeService;

    public HomeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping("/home")
    public String home(Model model,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "size", defaultValue = "3") int size) {
        try {
            model.addAttribute("recipes", recipeService.findAllRecipes(page, size));
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "index";
    }

}
