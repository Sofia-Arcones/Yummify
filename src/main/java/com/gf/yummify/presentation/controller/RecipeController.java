package com.gf.yummify.presentation.controller;

import com.gf.yummify.business.services.IngredientService;
import com.gf.yummify.business.services.RecipeService;
import com.gf.yummify.data.entity.Recipe;
import com.gf.yummify.data.enums.Difficulty;
import com.gf.yummify.data.enums.UnitOfMeasure;
import com.gf.yummify.presentation.dto.IngredientAutocompleteDTO;
import com.gf.yummify.presentation.dto.RecipeRequestDTO;
import com.gf.yummify.presentation.dto.RecipeResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
//TODO Implementar editado de recetas correctamente
@Controller
@RequestMapping("/recipe")
public class RecipeController {
    private RecipeService recipeService;
    private IngredientService ingredientService;

    public RecipeController(RecipeService recipeService, IngredientService ingredientService) {
        this.recipeService = recipeService;
        this.ingredientService = ingredientService;
    }

    /*@GetMapping("/create")
    public String recipeForm(Model model) {
        List<String> units = Arrays.stream(UnitOfMeasure.values()).map(Enum::name).collect(Collectors.toList());
        model.addAttribute("unidades", units);

        model.addAttribute("dificultades", Difficulty.values());

        List<IngredientAutocompleteDTO> ingredients = ingredientService.getApprovedIngredientsForAutocomplete();
        model.addAttribute("ingredientes", ingredients);

        return "recipes/createRecipeForm";
    }*/
    @GetMapping({"/create", "/edit/{id}"})
    public String recipeForm(@PathVariable(required = false) UUID id, Model model) {
        if (id != null) {
            // Editar receta existente
            RecipeResponseDTO recipe = recipeService.getRecipeResponseDTO(id);
            model.addAttribute("id", id);
            model.addAttribute("recipe", recipe);
        }
        // Cargar lista de unidades y dificultades
        model.addAttribute("unidades", UnitOfMeasure.values());
        model.addAttribute("dificultades", Difficulty.values());

        // Cargar lista de ingredientes para autocompletar
        List<IngredientAutocompleteDTO> ingredients = ingredientService.getApprovedIngredientsForAutocomplete();
        model.addAttribute("ingredientes", ingredients);

        return "recipes/createRecipeForm";
    }

    @PostMapping({"/", "/edit/{id}"})
    public String saveOrUpdateRecipe(@PathVariable(required = false) UUID id,
                                     @ModelAttribute @Valid RecipeRequestDTO requestDTO,
                                     Model model, Authentication authentication, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("recipeRequestDTO", requestDTO);
            return "recipes/createRecipeForm";
        }
        try {
            Recipe recipe = (id != null) ? recipeService.updateRecipe(id, requestDTO, authentication) : recipeService.saveRecipe(requestDTO, authentication);
            model.addAttribute("successMessage", id != null ? "Receta actualizada correctamente." : "Receta creada correctamente.");
            model.addAttribute("recipe", recipe);
            return "recipes/recipeSuccess";
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("recipeRequestDTO", requestDTO);
            return "recipes/createRecipeForm";
        }
    }

    /*@PostMapping()
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
*/
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

  /*  @GetMapping("/edit/{id}")
    public String updateRecipeForm(@PathVariable UUID id, Model model) {
        try {
            Recipe recipe = recipeService.findRecipeById(id);
            model.addAttribute("recipe", recipe);  // Pasa la receta al modelo
            model.addAttribute("unidades", UnitOfMeasure.values());
            model.addAttribute("dificultades", Difficulty.values());
            model.addAttribute("ingredientes", ingredientService.getApprovedIngredientsForAutocomplete());
            model.addAttribute("recipe", recipe);
        } catch (Exception ex) {
            model.addAttribute("error", "Error al cargar la receta");
        }
        return "recipes/createRecipeForm";
    }*/

}
