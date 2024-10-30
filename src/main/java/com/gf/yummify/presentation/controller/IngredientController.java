package com.gf.yummify.presentation.controller;

import com.gf.yummify.business.services.IngredientService;
import com.gf.yummify.data.entity.Ingredient;
import com.gf.yummify.data.enums.IngredientStatus;
import com.gf.yummify.data.enums.IngredientType;
import com.gf.yummify.data.enums.UnitOfMeasure;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
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
        if (ingredients.size() > 0) {
            for (Ingredient ingredient : ingredients) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                ingredient.setFormattedCreationDate(ingredient.getCreationDate().format(formatter));
                ingredient.setFormattedLastModification(ingredient.getLastModification().format(formatter));
            }
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
    public String updateIngredient(Model model, @ModelAttribute @Valid Ingredient ingredient) {
        try {
            ingredientService.updateIngredient(ingredient);
            model.addAttribute("updateMessage", "Ingrediente actualizado correctamente.");
            model.addAttribute("ingredient", ingredient);
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "ingredients/ingredientUpdate";
        }
        return "ingredients/ingredientSuccess";
    }
}
