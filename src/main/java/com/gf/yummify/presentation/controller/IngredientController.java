package com.gf.yummify.presentation.controller;

import com.gf.yummify.business.services.IngredientService;
import com.gf.yummify.data.entity.Ingredient;
import com.gf.yummify.data.enums.IngredientStatus;
import com.gf.yummify.data.enums.IngredientType;
import com.gf.yummify.data.enums.UnitOfMeasure;
import com.gf.yummify.presentation.dto.IngredientRequestDTO;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/ingredients")
public class IngredientController {

    private IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping("/management")
    public String ingredientManagement(Model model, @RequestParam(value = "status", required = false) String status, @RequestParam(value = "type", required = false) String type) {
        List<String> ingredientStatuses = Arrays.stream(IngredientStatus.values()).map(Enum::name).collect(Collectors.toList());
        model.addAttribute("statuses", ingredientStatuses);
        List<String> types = Arrays.stream(IngredientType.values()).map(Enum::name).collect(Collectors.toList());
        model.addAttribute("types", types);
        List<Ingredient> ingredients = new ArrayList<>();
        if (type != null && !type.isEmpty() && (status == null || status.isEmpty())) {
            ingredients = ingredientService.findIngredientsByType(IngredientType.valueOf(type));
        }
        if (status != null && !status.isEmpty() && (type == null || type.isEmpty())) {
            ingredients = ingredientService.findIngredientsByStatus(IngredientStatus.valueOf(status));
        }
        if (status != null && !status.isEmpty() && type != null && !type.isEmpty()) {
            ingredients = ingredientService.findIngredientsByStatusAndType(IngredientStatus.valueOf(status), IngredientType.valueOf(type));
        }

        //TODO CAMBIAR EL FORMATEO DE FECHAS A LA ENTIDAD
        if (ingredients.size() > 0) {
            model.addAttribute("ingredients", ingredients);
        }
        return "ingredients/ingredientManagement";
    }

    @GetMapping("/update/{id}")
    public String getIngredientUpdate(Model model, @PathVariable UUID id) {
        try {
            List<String> units = Arrays.stream(UnitOfMeasure.values()).map(Enum::name).collect(Collectors.toList());
            model.addAttribute("units", units);
            List<String> types = Arrays.stream(IngredientType.values()).map(Enum::name).collect(Collectors.toList());
            model.addAttribute("types", types);
            List<String> ingredientStatuses = Arrays.stream(IngredientStatus.values()).map(Enum::name).collect(Collectors.toList());
            model.addAttribute("statuses", ingredientStatuses);
            Ingredient ingredient = ingredientService.findIngredientById(id);
            model.addAttribute("ingredient", ingredient);
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "ingredients/ingredientUpdate";
    }

    @PostMapping("/update")
    public String updateIngredient(Model model,
                                   @ModelAttribute @Valid IngredientRequestDTO ingredient,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        try {
            Ingredient ingredientUpdated = ingredientService.updateIngredient(ingredient, authentication);
            model.addAttribute("updateMessage", "Ingrediente actualizado correctamente.");
            model.addAttribute("ingredient", ingredientUpdated);
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/ingredients/update/" + ingredient.getIngredientId();
        }
        return "ingredients/ingredientSuccess";
    }
}
