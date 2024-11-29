package com.gf.yummify.business.services;

import com.gf.yummify.business.mappers.RecipeMapper;
import com.gf.yummify.data.entity.*;
import com.gf.yummify.data.enums.*;
import com.gf.yummify.data.repository.FavoriteRecipeRepository;
import com.gf.yummify.data.repository.RecipeIngredientRepository;
import com.gf.yummify.data.repository.RecipeRepository;
import com.gf.yummify.data.repository.specifications.RecipeSpecifications;
import com.gf.yummify.presentation.dto.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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
    private final ActivityLogService activityLogService;

    private static final String UPLOAD_DIR = "src/main/resources/static/images/uploads/recipes";
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList("image/jpeg", "image/png");

    public RecipeServiceImpl(RecipeRepository recipeRepository, FavoriteRecipeRepository favoriteRecipeRepository, UserService userService, RecipeIngredientRepository recipeIngredientRepository, IngredientService ingredientService, TagService tagService, RecipeMapper recipeMapper, ActivityLogService activityLogService) {
        this.recipeRepository = recipeRepository;
        this.favoriteRecipeRepository = favoriteRecipeRepository;
        this.userService = userService;
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.ingredientService = ingredientService;
        this.tagService = tagService;
        this.recipeMapper = recipeMapper;
        this.activityLogService = activityLogService;
    }

    // ========================
    // Gestión de Recetas
    // ========================
    @Override
    public Recipe saveRecipe(RecipeRequestDTO recipeRequestDTO, Authentication authentication) {
        User user = userService.findUserByUsername(authentication.getName());
        Recipe recipe = setRecipeFields(recipeRequestDTO, user);
        recipe = recipeRepository.save(recipe);
        String description = "El usuario '" + user.getUsername() + "' ha subido la receta '" + recipe.getTitle() + "' (ID: " + recipe.getRecipeId() + ")";
        ActivityLogRequestDTO activityLogRequestDTO = new ActivityLogRequestDTO(user, recipe.getRecipeId(), RelatedEntity.RECIPE, ActivityType.RECIPE_CREATED, description);
        activityLogService.createActivityLog(activityLogRequestDTO);
        return recipe;
    }

    @Override
    public void deleteRecipe(UUID id, Authentication authentication) {
        if (authentication == null) {
            throw new IllegalArgumentException("Tienes que estar autentificado para hacer esta operación");
        }
        User user = userService.findUserByUsername(authentication.getName());
        Recipe recipe = findRecipeById(id);

        if (user != recipe.getUser() || (user != recipe.getUser() && !user.getRole().equals(Role.ROLE_ADMIN))) {
            throw new IllegalArgumentException("No tienes permisos para borrar esta receta");
        }
        deleteImageFile(recipe.getImage());
        String description = "El usuario '" + user.getUsername() + "' ha borrado la receta '" + recipe.getTitle() + "' (ID: " + recipe.getRecipeId() + ") del usuario '" + recipe.getUser().getUsername() + "'";
        ActivityLogRequestDTO activityLogRequestDTO = new ActivityLogRequestDTO(user, recipe.getUser().getUserId(), RelatedEntity.USER, ActivityType.RECIPE_DELETED, description);
        activityLogService.createActivityLog(activityLogRequestDTO);
        recipeRepository.delete(recipe);
    }

    @Override
    public Recipe findRecipeById(UUID id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("La receta con id: " + id + " no existe"));
    }

    @Override
    public Page<ShortRecipeResponseDTO> findFilteredRecipes(int page, int size, Difficulty difficulty,
                                                            Integer portions, List<String> tags,
                                                            List<String> ingredients, IngredientType ingredientType) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());
        Specification<Recipe> specification = Specification.where(null);

        if (difficulty != null) {
            specification = specification.and(RecipeSpecifications.hasDifficulty(difficulty));
        }
        if (portions != null) {
            specification = specification.and(RecipeSpecifications.hasPortions(portions));
        }
        if (tags != null) {
            specification = specification.and(RecipeSpecifications.hasTags(tags));
        }
        if (ingredients != null) {
            specification = specification.and(RecipeSpecifications.hasIngredients(ingredients));
        }
        if (ingredientType != null) {
            specification = specification.and(RecipeSpecifications.hasIngredientType(ingredientType));
        }

        Page<Recipe> recipePage = recipeRepository.findAll(specification, pageable);
        List<ShortRecipeResponseDTO> shortRecipeDTOs = recipePage
                .stream()
                .map(recipeMapper::toShortRecipeResponseDTO)
                .toList();
        return new PageImpl<>(shortRecipeDTOs, pageable, recipePage.getTotalElements());
    }

    @Override
    public Page<ShortRecipeResponseDTO> searchRecipes(String searchTerm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());
        Page<Recipe> recipePage = recipeRepository.findByTitleContainingIgnoreCase(searchTerm, pageable);
        List<ShortRecipeResponseDTO> shortRecipeDTOs = recipePage
                .stream()
                .map(recipeMapper::toShortRecipeResponseDTO)
                .toList();
        return new PageImpl<>(shortRecipeDTOs, pageable, recipePage.getTotalElements());
    }

    @Override
    public RecipeResponseDTO getRecipeResponseDTO(UUID id) {
        Recipe recipe = findRecipeById(id);
        return mapToRecipeResponseDTO(recipe);
    }

    @Override
    public List<Recipe> findRecipesByUser(Authentication authentication) {
        User user = userService.findUserByUsername(authentication.getName());
        return recipeRepository.findByUser(user);
    }

    @Override
    public void updateRecipe(RecipeRequestDTO recipeRequestDTO, Authentication authentication) {
        User user = userService.findUserByUsername(authentication.getName());
        Recipe recipe = findRecipeById(recipeRequestDTO.getRecipeId());

        if (!recipe.getUser().equals(user)) {
            throw new IllegalArgumentException("No puedes editar una receta que no es tuya.");
        }

        updateRecipeFields(recipeRequestDTO, recipe, user);

        recipe = recipeRepository.save(recipe);

        String description = "El usuario '" + user.getUsername() + "' ha actualizado la receta '" + recipe.getTitle() + "' (ID: " + recipe.getRecipeId() + ")";
        ActivityLogRequestDTO activityLogRequestDTO = new ActivityLogRequestDTO(user, recipe.getRecipeId(), RelatedEntity.RECIPE, ActivityType.RECIPE_UPDATED, description);
        activityLogService.createActivityLog(activityLogRequestDTO);
    }


    // ========================
    // Gestión de Favoritos
    // ========================

    @Override
    public void addOrDeleteRecipeFavorite(Authentication authentication, UUID recipeId) {
        Optional<FavoriteRecipe> favoriteRecipeOpt = findFavoriteRecipe(authentication, recipeId);
        if (authentication == null) {
            throw new IllegalArgumentException("Tienes que estar autentificado para hacer esta operación");
        }
        User user = userService.findUserByUsername(authentication.getName());
        String description = "";
        ActivityLogRequestDTO activityLogRequestDTO = new ActivityLogRequestDTO();
        if (favoriteRecipeOpt.isPresent()) {
            description = "Al usuario '" + user.getUsername() + "' le ha dejado de gustar la receta con ID: " + recipeId;
            activityLogRequestDTO = new ActivityLogRequestDTO(user, recipeId, RelatedEntity.RECIPE, ActivityType.RECIPE_UNFAVORITED, description);
            activityLogService.createActivityLog(activityLogRequestDTO);
            favoriteRecipeRepository.delete(favoriteRecipeOpt.get());
        } else {
            FavoriteRecipe favoriteRecipe = new FavoriteRecipe();
            favoriteRecipe.setUser(user);
            favoriteRecipe.setRecipe(findRecipeById(recipeId));
            favoriteRecipeRepository.save(favoriteRecipe);
            description = "Al usuario '" + user.getUsername() + "' le ha gustado la receta con ID: " + recipeId;
            activityLogRequestDTO = new ActivityLogRequestDTO(user, recipeId, RelatedEntity.RECIPE, ActivityType.RECIPE_FAVORITED, description);
            activityLogService.createActivityLog(activityLogRequestDTO);
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

    // ========================
    // Métodos Auxiliares
    // ========================

    private Recipe setRecipeFields(RecipeRequestDTO recipeRequestDTO, User user) {
        Recipe recipe = recipeMapper.toRecipe(recipeRequestDTO);
        recipe.setUser(user);
        List<RecipeIngredient> recipeIngredients = mapRecipeIngredients(recipeRequestDTO, user);
        for (RecipeIngredient recipeIngredient : recipeIngredients) {
            recipeIngredient.setRecipe(recipe);
        }
        recipe.setIngredients(recipeIngredients);
        recipe.setInstructions(joinInstructions(recipeRequestDTO.getInstructions()));

        if (!recipeRequestDTO.getTags().isEmpty()) {
            List<Tag> tagList = new ArrayList<>();
            for (String tag : recipeRequestDTO.getTags()) {
                tagList.add(tagService.findOrCreateTag(tag));
            }
            recipe.setTags(tagList);
        }

        MultipartFile image = recipeRequestDTO.getImage();
        recipe.setImage(handleImageUpload(image));
        return recipe;
    }

    private void updateRecipeFields(RecipeRequestDTO recipeRequestDTO, Recipe recipe, User user) {
        recipe.setTitle(recipeRequestDTO.getTitle());
        recipe.setDescription(recipeRequestDTO.getDescription());
        recipe.setPrepTime(recipeRequestDTO.getPrepTime());
        recipe.setPortions(recipeRequestDTO.getPortions());
        recipe.setDifficulty(Difficulty.valueOf(recipeRequestDTO.getDifficulty()));
        List<RecipeIngredient> recipeIngredients = recipe.getIngredients();
        for (RecipeIngredient recipeIngredient : recipeIngredients) {
            recipeIngredientRepository.delete(recipeIngredient);
        }

        List<RecipeIngredient> updatedIngredients = mapRecipeIngredients(recipeRequestDTO, user);
        for (RecipeIngredient ingredient : updatedIngredients) {
            ingredient.setRecipe(recipe);
            recipeIngredientRepository.save(ingredient);
        }
        recipe.setIngredients(updatedIngredients);

        recipe.setInstructions(joinInstructions(recipeRequestDTO.getInstructions()));
        System.out.println("TAGS: " + recipeRequestDTO.getTags());
        if (recipeRequestDTO.getTags() != null && !recipeRequestDTO.getTags().isEmpty()) {
            List<Tag> tagList = new ArrayList<>();
            for (String tagName : recipeRequestDTO.getTags()) {
                Tag tag = tagService.findOrCreateTag(tagName);
                if (!recipe.getTags().contains(tag)) {
                    tagList.add(tag);
                }
            }
            if (!tagList.isEmpty()) {
                recipe.getTags().addAll(tagList);
            }
        }

        MultipartFile image = recipeRequestDTO.getImage();
        if (image != null && !image.isEmpty()) {
            deleteImageFile(recipe.getImage());
            recipe.setImage(handleImageUpload(image));
        }
    }

    private List<RecipeIngredient> mapRecipeIngredients(RecipeRequestDTO recipeRequestDTO, User user) {
        List<RecipeIngredient> recipeIngredientList = new ArrayList<>();
        for (int i = 0; i < recipeRequestDTO.getIngredients().size(); i++) {
            Ingredient ingredient = ingredientService.findOrCreateIngredient(recipeRequestDTO.getIngredients().get(i), user);
            RecipeIngredient recipeIngredient = new RecipeIngredient();
            recipeIngredient.setIngredient(ingredient);
            recipeIngredient.setQuantity(recipeRequestDTO.getQuantities().get(i));
            recipeIngredient.setUnitOfMeasure(UnitOfMeasure.valueOf(recipeRequestDTO.getUnits().get(i)));
            recipeIngredientList.add(recipeIngredient);
        }
        return recipeIngredientList;
    }

    private RecipeResponseDTO mapToRecipeResponseDTO(Recipe recipe) {
        RecipeResponseDTO recipeResponseDTO = recipeMapper.toRecipeResponseDTO(recipe);

        recipeResponseDTO.setInstructions(getInstructions(recipe.getInstructions()));
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
        return recipeResponseDTO;
    }

    private String joinInstructions(List<String> instructionsList) {
        return String.join("|~|", instructionsList);
    }

    private List<String> getInstructions(String instructions) {
        return Arrays.asList(instructions.split("\\|~\\|"));
    }

    private String handleImageUpload(MultipartFile image) {
        if (image.isEmpty()) {
            throw new IllegalArgumentException("La receta debe tener una imagen");
        }
        if (!isImageValid(image)) {
            throw new IllegalArgumentException("Tipo de archivo no permitido o archivo vacío. Solo se permiten: JPEG, PNG.");
        }
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            boolean dirCreated = uploadDir.mkdirs();
            if (!dirCreated) {
                throw new RuntimeException("No se pudo crear el directorio de subida: " + UPLOAD_DIR);
            }
        }
        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
        File outputFile = new File(UPLOAD_DIR, fileName);

        try {
            InputStream inputStream = image.getInputStream();
            BufferedImage bufferedImage = ImageIO.read(inputStream);

            int width = 1200;
            int height = 800;
            Image resizedImage = bufferedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);

            BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = outputImage.createGraphics();
            graphics.drawImage(resizedImage, 0, 0, null);
            graphics.dispose();

            ImageIO.write(outputImage, "jpg", outputFile);

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
}
