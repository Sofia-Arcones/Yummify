package com.gf.yummify.presentation.controller;

import com.gf.yummify.business.services.IngredientService;
import com.gf.yummify.data.entity.Ingredient;
import com.gf.yummify.data.enums.IngredientStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/ingredients")
public class IngredientController {

    private IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping("/management")
    public String ingredientManagement(Model model, @RequestParam(value = "status", required = false)  String status) {
        List<String> ingredientStatuses = Arrays.stream(IngredientStatus.values()).map(Enum::name).collect(Collectors.toList());
        model.addAttribute("statuses", ingredientStatuses);
        if (status != null && !status.isEmpty()){
            List<Ingredient> ingredients = ingredientService.findIngredientsByStatus(IngredientStatus.valueOf(status));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            for (Ingredient ingredient : ingredients) {
                ingredient.setFormattedCreationDate(ingredient.getCreationDate().format(formatter));
                ingredient.setFormattedLastModification(ingredient.getLastModification().format(formatter));
            }
            model.addAttribute("ingredients", ingredients);
        }
        return "ingredientManagement";
    }


}
