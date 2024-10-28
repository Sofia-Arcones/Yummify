package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.Ingredient;
import com.gf.yummify.data.entity.Recipe;
import com.gf.yummify.data.entity.RecipeIngredient;
import com.gf.yummify.data.entity.User;
import com.gf.yummify.data.enums.Difficulty;
import com.gf.yummify.data.enums.IngredientStatus;
import com.gf.yummify.data.enums.UnitOfMeasure;
import com.gf.yummify.data.repository.IngredientRepository;
import com.gf.yummify.data.repository.RecipeIngredientRepository;
import com.gf.yummify.data.repository.RecipeRepository;
import com.gf.yummify.data.repository.UserRepository;
import com.gf.yummify.presentation.dto.RecipeIngredientDTO;
import com.gf.yummify.presentation.dto.RecipeRequestDTO;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class RecipeServiceImpl implements RecipeService {

    private RecipeRepository recipeRepository;
    private UserRepository userRepository;
    private RecipeIngredientRepository recipeIngredientRepository;
    private IngredientRepository ingredientRepository;

    public RecipeServiceImpl(RecipeRepository recipeRepository, UserRepository userRepository, RecipeIngredientRepository recipeIngredientRepository, IngredientRepository ingredientRepository) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.ingredientRepository = ingredientRepository;
    }

    //TODO
    //Manejar la imagen que aun no lo he hecho
    @Override
    public void saveRecipe(RecipeRequestDTO recipeRequestDTO, Authentication authentication) {
        Optional<User> user = userRepository.findByUsername(authentication.getName());
        if (user.isPresent()) {
            Recipe recipe = new Recipe();
            recipe.setTitle(recipeRequestDTO.getTitle());
            recipe.setDescription(recipeRequestDTO.getDescription());
            recipe.setDifficulty(Difficulty.valueOf(recipeRequestDTO.getDifficulty()));
            recipe.setPrepTime(recipeRequestDTO.getPrepTime());
            recipe.setUser(user.get());

            String instructions = joinInstrucions(recipeRequestDTO.getInstructions());
            recipe.setInstructions(instructions);

            List<String> ingredients = recipeRequestDTO.getIngredients();
            List<RecipeIngredientDTO> recipeIngredients = new ArrayList<>();
            for (int i = 0; i < ingredients.size(); i++) {
                RecipeIngredientDTO recipeIngredientDTO = new RecipeIngredientDTO(ingredients.get(i), recipeRequestDTO.getQuantities().get(i), UnitOfMeasure.valueOf(recipeRequestDTO.getUnits().get(i)));
                recipeIngredients.add(recipeIngredientDTO);
            }

            List<RecipeIngredient> recipeIngredientList = createRecipeIngredients(recipeIngredients);
            recipe.setIngredients(recipeIngredientList);
            recipeRepository.save(recipe);
        }
    }

    @Override
    public List<RecipeIngredient> createRecipeIngredients(List<RecipeIngredientDTO> ingredients) {

        List<RecipeIngredient> recipeIngredientList = new ArrayList<>();
        for (RecipeIngredientDTO recipeIngredientDTO : ingredients) {
            if (ingredientRepository.existsByIngredientName(recipeIngredientDTO.getIngredientName())) {
                Ingredient ingredient = ingredientRepository.findByIngredientName(recipeIngredientDTO.getIngredientName()).get();
                RecipeIngredient recipeIngredient = new RecipeIngredient();
                recipeIngredient.setIngredient(ingredient);
                recipeIngredient.setQuantity(recipeIngredientDTO.getQuantity());
                recipeIngredient.setUnitOfMeasure(recipeIngredientDTO.getUnitOfMeasure());
                recipeIngredientList.add(recipeIngredient);
            } else {
                Ingredient ingredient = new Ingredient();
                ingredient.setIngredientName(recipeIngredientDTO.getIngredientName());
                ingredient.setIngredientStatus(IngredientStatus.PENDING_REVIEW);
                ingredient = ingredientRepository.save(ingredient);
                RecipeIngredient recipeIngredient = new RecipeIngredient();
                recipeIngredient.setIngredient(ingredient);
                recipeIngredient.setQuantity(recipeIngredientDTO.getQuantity());
                recipeIngredient.setUnitOfMeasure(recipeIngredientDTO.getUnitOfMeasure());
                recipeIngredientList.add(recipeIngredient);
            }
        }
        return recipeIngredientList;
    }

    @Override
    public String joinInstrucions(List<String> instructionsList) {
        String instructions = String.join("|~|", instructionsList);
        return instructions;
    }

    @Override
    public List<String> getInstructions(Recipe recipe) {
        return Arrays.asList(recipe.getInstructions().split("|~|"));
    }

}
