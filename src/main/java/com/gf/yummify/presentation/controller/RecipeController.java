package com.gf.yummify.presentation.controller;

import com.gf.yummify.business.services.RecipeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RecipeController {
    private RecipeService recipeService;

    @GetMapping("/recipe/create")
    public String recipeCreation(){
        return "recipeCreation";
    }
    @GetMapping("/header")
    public String header(){
        return "header";
    }

}
