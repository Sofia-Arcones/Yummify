package com.gf.yummify.presentation.controller;

import com.gf.yummify.business.services.IngredientService;
import com.gf.yummify.business.services.RatingService;
import com.gf.yummify.business.services.RecipeService;
import com.gf.yummify.business.services.ShoppingListService;
import com.gf.yummify.data.entity.Recipe;
import com.gf.yummify.data.enums.Difficulty;
import com.gf.yummify.data.enums.UnitOfMeasure;
import com.gf.yummify.presentation.dto.IngredientAutocompleteDTO;
import com.gf.yummify.presentation.dto.RecipeRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
    private RatingService ratingService;

    public RecipeController(RecipeService recipeService, IngredientService ingredientService, ShoppingListService shoppingListService, RatingService ratingService) {
        this.recipeService = recipeService;
        this.ingredientService = ingredientService;
        this.shoppingListService = shoppingListService;
        this.ratingService = ratingService;
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
    public String createRecipe(@ModelAttribute @Valid RecipeRequestDTO requestDTO, Model model, Authentication authentication, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("recipeRequestDTO", requestDTO);
            return "recipes/createRecipeForm";
        }
        try {
            Recipe recipe = recipeService.saveRecipe(requestDTO, authentication);
            model.addAttribute("successMessage", "Receta creada correctamente.");
            model.addAttribute("recipe", recipe);
            return "recipes/recipeSuccess";
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("recipeRequestDTO", requestDTO);
            return "recipes/createRecipeForm";
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
            model.addAttribute("shoppingLists", shoppingListService.findListsByUser(authentication));
            model.addAttribute("recipe", recipeService.getRecipeResponseDTO(id));
            model.addAttribute("recipeId", id);
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "recipes/recipe";
    }

    @PostMapping("/addRating")
    public String addRating(@RequestParam UUID recipeId,
                            @RequestParam Double rating,
                            @RequestParam(required = false) String comment,
                            Authentication authentication,
                            RedirectAttributes redirectAttributes,
                            HttpServletRequest request) {
        try {
            ratingService.addRating(recipeId, rating, comment, authentication);
            redirectAttributes.addFlashAttribute("ratingSuccess", "Tu valoración ha sido añadida.");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            redirectAttributes.addFlashAttribute("ratingError", ex.getMessage());
        }
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/home");
    }


}
