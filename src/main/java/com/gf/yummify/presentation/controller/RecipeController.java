package com.gf.yummify.presentation.controller;

import com.gf.yummify.business.services.IngredientService;
import com.gf.yummify.business.services.RecipeService;
import com.gf.yummify.data.entity.Ingredient;
import com.gf.yummify.data.enums.Difficulty;
import com.gf.yummify.data.enums.IngredientStatus;
import com.gf.yummify.data.enums.UnitOfMeasure;
import com.gf.yummify.presentation.dto.RecipeRequestDTO;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class RecipeController {
    private RecipeService recipeService;
    private IngredientService ingredientService;

    public RecipeController(RecipeService recipeService, IngredientService ingredientService) {
        this.recipeService = recipeService;
        this.ingredientService = ingredientService;
    }

    @GetMapping("/recipe/create")
    public String recipeForm(Model model) {
        List<String> unidades = Arrays.stream(UnitOfMeasure.values()).map(Enum::name).collect(Collectors.toList());
        model.addAttribute("unidades", unidades);

        model.addAttribute("dificultades", Difficulty.values());

        List<Ingredient> ingredients = ingredientService.findIngredientsByStatus(IngredientStatus.APPROVED);
        List<String> ingredientNames = ingredients.stream().map(Ingredient::getIngredientName).collect(Collectors.toList());
        model.addAttribute("ingredientes", ingredientNames);

        return "createRecipeForm";
    }

    @PostMapping("/recipe")
    public void crearReceta(@ModelAttribute RecipeRequestDTO requestDTO, Model model, Authentication authentication) {

}

}
