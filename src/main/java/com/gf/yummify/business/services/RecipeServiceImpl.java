package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.Recipe;
import com.gf.yummify.data.repository.RecipeRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class RecipeServiceImpl implements RecipeService{

    private RecipeRepository recipeRepository;

    public void saveRecipe(Recipe recipe, List<String> instructionsList) {
        String instructions = String.join(";", instructionsList); // Une la lista con un delimitador
        recipe.setInstructions(instructions);
        recipeRepository.save(recipe);
    }
    public List<String> getInstructions(Recipe recipe) {
        return Arrays.asList(recipe.getInstructions().split(";")); // Divide la cadena en una lista usando el mismo delimitador
    }

}
