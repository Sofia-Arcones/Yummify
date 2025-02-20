package com.gf.yummify.presentation.controller;

import com.gf.yummify.business.services.IngredientService;
import com.gf.yummify.business.services.RecipeService;
import com.gf.yummify.business.services.TagService;
import com.gf.yummify.data.enums.Difficulty;
import com.gf.yummify.data.enums.IngredientType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    private RecipeService recipeService;
    private TagService tagService;
    private IngredientService ingredientService;

    public HomeController(RecipeService recipeService, TagService tagService, IngredientService ingredientService) {
        this.recipeService = recipeService;
        this.tagService = tagService;
        this.ingredientService = ingredientService;
    }

    @GetMapping("/home")
    public String home(Model model,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "size", defaultValue = "3") int size,
                       @RequestParam(value = "difficulty", required = false) Difficulty difficulty,
                       @RequestParam(value = "portions", required = false) Integer portions,
                       @RequestParam(value = "tags", required = false) List<String> tags,
                       @RequestParam(value = "ingredients", required = false) List<String> ingredients,
                       @RequestParam(value = "ingredientType", required = false) IngredientType ingredientType,
                       @RequestParam(value = "search", required = false) String search,
                       @RequestParam(value = "isAjax", defaultValue = "false") boolean isAjax) {
        try {
            System.out.println("Search term: " + search);
            model.addAttribute("ingredientTypes", IngredientType.values());
            model.addAttribute("difficulties", Difficulty.values());

            if (search != null && !search.isEmpty()) {
                model.addAttribute("searchTerm", search);
                model.addAttribute("recipes", recipeService.searchRecipes(search, page, size));
            } else {
                model.addAttribute("recipes", recipeService.findFilteredRecipes(page, size, difficulty, portions, tags, ingredients, ingredientType));
            }

            if (isAjax) {
                return "fragments/recipeContainer :: recipesContainer";
            }
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "index";
    }
}
