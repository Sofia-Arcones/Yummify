package com.gf.yummify.presentation.controller;

import com.gf.yummify.business.services.IngredientService;
import com.gf.yummify.business.services.RecipeService;
import com.gf.yummify.data.entity.Recipe;
import com.gf.yummify.data.enums.Difficulty;
import com.gf.yummify.data.enums.UnitOfMeasure;
import com.gf.yummify.presentation.dto.IngredientAutocompleteDTO;
import com.gf.yummify.presentation.dto.RecipeRequestDTO;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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
        List<String> units = Arrays.stream(UnitOfMeasure.values()).map(Enum::name).collect(Collectors.toList());
        model.addAttribute("unidades", units);

        model.addAttribute("dificultades", Difficulty.values());

        List<IngredientAutocompleteDTO> ingredients = ingredientService.getApprovedIngredientsForAutocomplete();
        model.addAttribute("ingredientes", ingredients);

        return "createRecipeForm";
    }

    @PostMapping("/recipe")
    public String crearReceta(@ModelAttribute @Valid RecipeRequestDTO requestDTO, Model model, Authentication authentication, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("recipeRequestDTO", requestDTO);
            return "createRecipeForm";
        }
        try {
            Recipe recipe = recipeService.saveRecipe(requestDTO, authentication);
            model.addAttribute("successMessage", "Receta creada correctamente.");
            model.addAttribute("recipe", recipe);
            return "recipeSuccess";
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("recipeRequestDTO", requestDTO);
            return "createRecipeForm";
        }
    }

}
