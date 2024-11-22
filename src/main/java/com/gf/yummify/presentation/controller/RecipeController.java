package com.gf.yummify.presentation.controller;

import com.gf.yummify.business.services.IngredientService;
import com.gf.yummify.business.services.RecipeService;
import com.gf.yummify.business.services.ShoppingListService;
import com.gf.yummify.data.entity.Recipe;
import com.gf.yummify.data.enums.Difficulty;
import com.gf.yummify.data.enums.UnitOfMeasure;
import com.gf.yummify.presentation.dto.FavoriteRecipeDTO;
import com.gf.yummify.presentation.dto.IngredientAutocompleteDTO;
import com.gf.yummify.presentation.dto.RecipeRequestDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/recipe")
public class RecipeController {
    private RecipeService recipeService;
    private IngredientService ingredientService;
    private ShoppingListService shoppingListService;

    public RecipeController(RecipeService recipeService, IngredientService ingredientService, ShoppingListService shoppingListService) {
        this.recipeService = recipeService;
        this.ingredientService = ingredientService;
        this.shoppingListService = shoppingListService;
    }

    @GetMapping("/create")
    public String recipeForm(Model model) {
        List<String> units = Arrays.stream(UnitOfMeasure.values()).map(Enum::name).collect(Collectors.toList());
        model.addAttribute("unidades", units);

        model.addAttribute("dificultades", Difficulty.values());

        List<IngredientAutocompleteDTO> ingredients = ingredientService.getApprovedIngredientsForAutocomplete();
        model.addAttribute("ingredientes", ingredients);

        return "recipes/createRecipeForm";
    }

    @PostMapping()
    public String createRecipe(@ModelAttribute @Valid RecipeRequestDTO requestDTO, Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            Recipe recipe = recipeService.saveRecipe(requestDTO, authentication);
            model.addAttribute("successMessage", "Receta creada correctamente.");
            model.addAttribute("recipe", recipe);
            return "recipes/recipeSuccess";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            redirectAttributes.addFlashAttribute("recipe", requestDTO);
            return "redirect:/recipe/create";
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteRecipe(@PathVariable UUID id) {
        Map<String, String> response = new HashMap<>();
        try {
            recipeService.deleteRecipe(id);
            response.put("success", "Eliminado correctamente");
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            response.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    public String showRecipe(@PathVariable UUID id, Model model, Authentication authentication) {
        try {
            if (authentication != null) {
                model.addAttribute("shoppingLists", shoppingListService.findListsByUser(authentication));
                model.addAttribute("isFavorited", recipeService.findRecipeFavorite(authentication, id));
            }
            model.addAttribute("recipe", recipeService.getRecipeResponseDTO(id));
            model.addAttribute("recipeId", id);
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "recipes/recipe";
    }

    @PostMapping("/favorite")
    public String addOrDeleteFavoriteRecipe(@RequestParam("recipeId") UUID recipeId,
                                            Model model, RedirectAttributes redirectAttributes,
                                            Authentication authentication) {
        try {
            recipeService.addOrDeleteRecipeFavorite(authentication, recipeId);
            redirectAttributes.addAttribute("id", recipeId);
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/recipe/{id}";
    }

    @GetMapping("/favorites")
    public String showFavoriteRecipes(Authentication authentication,
                                      Model model,
                                      @RequestParam(value = "page", defaultValue = "0") int page,
                                      @RequestParam(value = "size", defaultValue = "3") int size) {
        try {
            Page<FavoriteRecipeDTO> favoriteRecipeDTOPage = recipeService.findAllFavorites(authentication, page, size);
            model.addAttribute("recipes", favoriteRecipeDTOPage);
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "/recipes/favoriteRecipes";
    }
}
