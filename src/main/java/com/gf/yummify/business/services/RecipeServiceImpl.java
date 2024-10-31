package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.Ingredient;
import com.gf.yummify.data.entity.Recipe;
import com.gf.yummify.data.entity.RecipeIngredient;
import com.gf.yummify.data.entity.User;
import com.gf.yummify.data.enums.Difficulty;
import com.gf.yummify.data.enums.UnitOfMeasure;
import com.gf.yummify.data.repository.RecipeIngredientRepository;
import com.gf.yummify.data.repository.RecipeRepository;
import com.gf.yummify.presentation.dto.RecipeRequestDTO;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class RecipeServiceImpl implements RecipeService {

    private RecipeRepository recipeRepository;
    private UserService userService;
    private RecipeIngredientRepository recipeIngredientRepository;
    private IngredientService ingredientService;
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/recipes";
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList("image/jpeg", "image/png");

    public RecipeServiceImpl(RecipeRepository recipeRepository, UserService userService, RecipeIngredientRepository recipeIngredientRepository, IngredientService ingredientService) {
        this.recipeRepository = recipeRepository;
        this.userService = userService;
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.ingredientService = ingredientService;
    }

    @Override
    public Recipe saveRecipe(RecipeRequestDTO recipeRequestDTO, Authentication authentication) {
        User user = userService.findUserByUsername(authentication.getName());
        Recipe recipe = new Recipe();
        recipe.setTitle(recipeRequestDTO.getTitle());
        recipe.setDescription(recipeRequestDTO.getDescription());
        recipe.setDifficulty(Difficulty.valueOf(recipeRequestDTO.getDifficulty()));
        recipe.setPrepTime(recipeRequestDTO.getPrepTime());
        recipe.setPortions(recipeRequestDTO.getPortions());
        recipe.setUser(user);

        recipe.setInstructions(joinInstrucions(recipeRequestDTO.getInstructions()));
        // Procesar los ingredientes
        List<RecipeIngredient> recipeIngredients = mapRecipeIngredients(recipeRequestDTO);
        for (RecipeIngredient recipeIngredient : recipeIngredients) {
            recipeIngredient.setRecipe(recipe);
        }
        recipe.setIngredients(recipeIngredients);
        // Manejo de la imagen
        MultipartFile image = recipeRequestDTO.getImage();
        if (image.isEmpty()) {
            throw new IllegalArgumentException("La receta debe tener una imagen");
        }
        recipe.setImage(handleImageUpload(image));
        return recipeRepository.save(recipe);
    }

    private List<RecipeIngredient> mapRecipeIngredients(RecipeRequestDTO recipeRequestDTO) {
        List<RecipeIngredient> recipeIngredientList = new ArrayList<>();

        for (int i = 0; i < recipeRequestDTO.getIngredients().size(); i++) {
            String rawIngredientName = recipeRequestDTO.getIngredients().get(i);
            Double quantity = recipeRequestDTO.getQuantities().get(i);
            UnitOfMeasure unit = UnitOfMeasure.valueOf(recipeRequestDTO.getUnits().get(i));

            Ingredient ingredient = ingredientService.findOrCreateIngredient(rawIngredientName);

            RecipeIngredient recipeIngredient = new RecipeIngredient();
            recipeIngredient.setIngredient(ingredient);
            recipeIngredient.setQuantity(quantity);
            recipeIngredient.setUnitOfMeasure(unit);
            recipeIngredientList.add(recipeIngredient);
        }

        return recipeIngredientList;
    }

    private String handleImageUpload(MultipartFile image) {
        if (!isImageValid(image)) {
            throw new IllegalArgumentException("Tipo de archivo no permitido o archivo vacío. Solo se permiten: JPEG, PNG.");
        }

        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
        Path imagePath = Paths.get(UPLOAD_DIR, fileName);

        try {
            Files.createDirectories(imagePath.getParent());
            Files.write(imagePath, image.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar la imagen: " + e.getMessage(), e);
        }

        return imagePath.toString();
    }

    private boolean isImageValid(MultipartFile image) {
        String fileName = image.getOriginalFilename();
        String contentType = image.getContentType();

        return fileName != null && !image.isEmpty() &&
                fileName.toLowerCase().matches(".*\\.(jpg|jpeg|png)$") &&
                ALLOWED_CONTENT_TYPES.contains(contentType);
    }

    private String joinInstrucions(List<String> instructionsList) {
        String instructions = String.join("|~|", instructionsList);
        return instructions;
    }

    private List<String> getInstructions(Recipe recipe) {
        return Arrays.asList(recipe.getInstructions().split("|~|"));
    }

}
