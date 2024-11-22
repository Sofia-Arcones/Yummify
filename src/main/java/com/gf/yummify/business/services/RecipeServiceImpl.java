package com.gf.yummify.business.services;

import com.gf.yummify.business.mappers.RecipeMapper;
import com.gf.yummify.data.entity.*;
import com.gf.yummify.data.enums.Difficulty;
import com.gf.yummify.data.enums.UnitOfMeasure;
import com.gf.yummify.data.repository.FavoriteRecipeRepository;
import com.gf.yummify.data.repository.RecipeIngredientRepository;
import com.gf.yummify.data.repository.RecipeRepository;
import com.gf.yummify.presentation.dto.FavoriteRecipeDTO;
import com.gf.yummify.presentation.dto.RecipeRequestDTO;
import com.gf.yummify.presentation.dto.RecipeResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final FavoriteRecipeRepository favoriteRecipeRepository;
    private final UserService userService;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final IngredientService ingredientService;
    private final TagService tagService;
    private final RecipeMapper recipeMapper;

    private static final String UPLOAD_DIR = "src/main/resources/static/images/uploads/recipes";
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList("image/jpeg", "image/png");

    public RecipeServiceImpl(RecipeRepository recipeRepository, FavoriteRecipeRepository favoriteRecipeRepository, UserService userService, RecipeIngredientRepository recipeIngredientRepository, IngredientService ingredientService, TagService tagService, RecipeMapper recipeMapper) {
        this.recipeRepository = recipeRepository;
        this.favoriteRecipeRepository = favoriteRecipeRepository;
        this.userService = userService;
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.ingredientService = ingredientService;
        this.tagService = tagService;
        this.recipeMapper = recipeMapper;
    }

    @Override
    public Recipe saveRecipe(RecipeRequestDTO recipeRequestDTO, Authentication authentication) {
        User user = userService.findUserByUsername(authentication.getName());
        Recipe recipe = new Recipe();
        setRecipeFields(recipe, recipeRequestDTO, user);

        MultipartFile image = recipeRequestDTO.getImage();
        if (image.isEmpty()) {
            throw new IllegalArgumentException("La receta debe tener una imagen");
        }
        recipe.setImage(handleImageUpload(image));

        return recipeRepository.save(recipe);
    }

    @Override
    public void deleteRecipe(UUID id) {
        Recipe recipe = findRecipeById(id);
        deleteImageFile(recipe.getImage());
        recipeRepository.delete(recipe);
    }

    @Override
    public Recipe findRecipeById(UUID id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("La receta con id: " + id + " no existe"));
    }

    @Override
    public List<Recipe> findRecipesByUser(Authentication authentication) {
        User user = userService.findUserByUsername(authentication.getName());
        return recipeRepository.findByUser(user);
    }

    @Override
    public RecipeResponseDTO getRecipeResponseDTO(UUID id) {
        Recipe recipe = findRecipeById(id);
        return mapToRecipeResponseDTO(recipe);
    }

    @Override
    public void addOrDeleteRecipeFavorite(Authentication authentication, UUID recipeId) {
        Optional<FavoriteRecipe> favoriteRecipeOpt = findFavoriteRecipe(authentication, recipeId);
        if (favoriteRecipeOpt.isPresent()) {
            favoriteRecipeRepository.delete(favoriteRecipeOpt.get());
        } else {
            FavoriteRecipe favoriteRecipe = new FavoriteRecipe();
            favoriteRecipe.setUser(userService.findUserByUsername(authentication.getName()));
            favoriteRecipe.setRecipe(findRecipeById(recipeId));
            favoriteRecipeRepository.save(favoriteRecipe);
        }
    }

    @Override
    public Boolean findRecipeFavorite(Authentication authentication, UUID recipeId) {
        return findFavoriteRecipe(authentication, recipeId).isPresent();
    }

    @Override
    public Page<FavoriteRecipeDTO> findAllFavorites(Authentication authentication, int page, int size) {
        User user = userService.findUserByUsername(authentication.getName());
        List<FavoriteRecipe> favoriteRecipeList = user.getFavoriteRecipes();
        List<FavoriteRecipeDTO> favoriteRecipeDTOS = favoriteRecipeList.stream()
                .map(recipeMapper::toFavoriteRecipeDTO)
                .collect(Collectors.toList());
        int start = (int) PageRequest.of(page, size).getOffset();
        int end = Math.min((start + size), favoriteRecipeDTOS.size());
        if (start > end) {
            return new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), favoriteRecipeDTOS.size());
        }
        List<FavoriteRecipeDTO> subList = favoriteRecipeDTOS.subList(start, end);
        return new PageImpl<>(subList, PageRequest.of(page, size), favoriteRecipeDTOS.size());
    }

    private Optional<FavoriteRecipe> findFavoriteRecipe(Authentication authentication, UUID recipeId) {
        User user = userService.findUserByUsername(authentication.getName());
        Recipe recipe = findRecipeById(recipeId);
        return favoriteRecipeRepository.findByUserAndRecipe(user, recipe);
    }

    private void setRecipeFields(Recipe recipe, RecipeRequestDTO recipeRequestDTO, User user) {
        recipe.setTitle(recipeRequestDTO.getTitle());
        recipe.setDescription(recipeRequestDTO.getDescription());
        recipe.setDifficulty(Difficulty.valueOf(recipeRequestDTO.getDifficulty()));
        recipe.setPrepTime(recipeRequestDTO.getPrepTime());
        recipe.setPortions(recipeRequestDTO.getPortions());
        recipe.setUser(user);

        List<RecipeIngredient> recipeIngredients = mapRecipeIngredients(recipeRequestDTO);
        recipeIngredients.forEach(ri -> ri.setRecipe(recipe));
        recipe.setIngredients(recipeIngredients);

        recipe.setInstructions(joinInstructions(recipeRequestDTO.getInstructions()));
        if (!recipeRequestDTO.getTags().isEmpty()) {
            List<Tag> tagList = new ArrayList<>();
            for (String tag : recipeRequestDTO.getTags()) {
                tagList.add(tagService.findOrCreateTag(tag));
            }
            recipe.setTags(tagList);
        }
    }

    private List<RecipeIngredient> mapRecipeIngredients(RecipeRequestDTO recipeRequestDTO) {
        List<RecipeIngredient> recipeIngredientList = new ArrayList<>();

        for (int i = 0; i < recipeRequestDTO.getIngredients().size(); i++) {
            Ingredient ingredient = ingredientService.findOrCreateIngredient(recipeRequestDTO.getIngredients().get(i));
            RecipeIngredient recipeIngredient = new RecipeIngredient();
            recipeIngredient.setIngredient(ingredient);
            recipeIngredient.setQuantity(recipeRequestDTO.getQuantities().get(i));
            recipeIngredient.setUnitOfMeasure(UnitOfMeasure.valueOf(recipeRequestDTO.getUnits().get(i)));
            recipeIngredientList.add(recipeIngredient);
        }

        return recipeIngredientList;
    }

    private RecipeResponseDTO mapToRecipeResponseDTO(Recipe recipe) {
        RecipeResponseDTO recipeResponseDTO = new RecipeResponseDTO();
        recipeResponseDTO.setDescription(recipe.getDescription());
        recipeResponseDTO.setPortions(recipe.getPortions());
        recipeResponseDTO.setDifficulty(recipe.getDifficulty().toString());
        recipeResponseDTO.setTitle(recipe.getTitle());
        recipeResponseDTO.setPrepTime(recipe.getPrepTime());
        recipeResponseDTO.setInstructions(getInstructions(recipe.getInstructions()));
        recipeResponseDTO.setImage(recipe.getImage());
        recipeResponseDTO.setRatings(recipe.getRatings());
        recipeResponseDTO.setAverage(calculateAverage(recipe.getRatings()));

        List<String> ingredients = new ArrayList<>();
        List<Double> quantities = new ArrayList<>();
        List<String> units = new ArrayList<>();

        for (RecipeIngredient recipeIngredient : recipe.getIngredients()) {
            ingredients.add(recipeIngredient.getIngredient().getIngredientName());
            quantities.add(recipeIngredient.getQuantity());
            units.add(recipeIngredient.getUnitOfMeasure().name());
        }
        recipeResponseDTO.setIngredients(ingredients);
        recipeResponseDTO.setQuantities(quantities);
        recipeResponseDTO.setUnits(units);

        List<String> tags = new ArrayList<>();
        for (Tag tag : recipe.getTags()) {
            tags.add(tag.getName());
        }
        System.out.println("Total de valoraciones: " + recipeResponseDTO.getRatings().size());
        System.out.println("Promedio de valoraciones: " + recipeResponseDTO.getAverage());
        recipeResponseDTO.setTags(tags);
        return recipeResponseDTO;
    }

    private double calculateAverage(List<Rating> ratings) {
        List<Double> rates = new ArrayList<>();
        for (Rating rate : ratings) {
            rates.add(rate.getRating());
        }
        return rates.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    private String joinInstructions(List<String> instructionsList) {
        return String.join("|~|", instructionsList);
    }

    private List<String> getInstructions(String instructions) {
        return Arrays.asList(instructions.split("\\|~\\|"));
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

        return "/images/uploads/recipes/" + fileName;
    }

    private boolean isImageValid(MultipartFile image) {
        String fileName = image.getOriginalFilename();
        String contentType = image.getContentType();

        return fileName != null && !image.isEmpty() &&
                fileName.toLowerCase().matches(".*\\.(jpg|jpeg|png)$") &&
                ALLOWED_CONTENT_TYPES.contains(contentType);
    }

    private void deleteImageFile(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            Path path = Paths.get("src/main/resources/static" + imagePath);

            try {
                if (Files.exists(path)) {
                    Files.delete(path);
                }
            } catch (IOException e) {
                throw new RuntimeException("Error al eliminar la imagen: " + e.getMessage(), e);
            }
        }
    }
}
