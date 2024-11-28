package com.gf.yummify.business.services;

import com.gf.yummify.business.mappers.IngredientMapper;
import com.gf.yummify.data.entity.Ingredient;
import com.gf.yummify.data.entity.User;
import com.gf.yummify.data.enums.*;
import com.gf.yummify.data.repository.IngredientRepository;
import com.gf.yummify.presentation.dto.ActivityLogRequestDTO;
import com.gf.yummify.presentation.dto.IngredientAutocompleteDTO;
import com.gf.yummify.presentation.dto.IngredientRequestDTO;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class IngredientServiceImpl implements IngredientService {
    private IngredientRepository ingredientRepository;
    private ActivityLogService activityLogService;
    private UserService userService;

    private IngredientMapper ingredientMapper;

    public IngredientServiceImpl(IngredientRepository ingredientRepository, ActivityLogService activityLogService, UserService userService, IngredientMapper ingredientMapper) {
        this.ingredientRepository = ingredientRepository;
        this.activityLogService = activityLogService;
        this.userService = userService;
        this.ingredientMapper = ingredientMapper;
    }

    @Override
    public Ingredient findByIngredientName(String ingredientName) {
        String normalizedIngredientName = capitalizeIngredientName(ingredientName);
        Optional<Ingredient> ingredient = ingredientRepository.findByIngredientName(normalizedIngredientName);
        if (ingredient.isPresent()) {
            return ingredient.get();
        } else {
            throw new NoSuchElementException("No existe ese ingrediente");
        }
    }

    @Override
    public Ingredient findOrCreateIngredient(String name, User user) {
        String normalizedIngredientName = capitalizeIngredientName(name);
        return ingredientRepository.findByIngredientName(normalizedIngredientName)
                .orElseGet(() -> {
                    Ingredient newIngredient = new Ingredient();
                    newIngredient.setIngredientName(normalizedIngredientName);
                    newIngredient.setIngredientStatus(IngredientStatus.PENDING_REVIEW);
                    newIngredient = ingredientRepository.save(newIngredient);
                    String description = "El usuario '" + user.getUsername() + "' ha sugerido el ingrediente '" + newIngredient.getIngredientName() + "' (ID: " + newIngredient.getIngredientId() + ")";
                    ActivityLogRequestDTO activityLogRequestDTO = new ActivityLogRequestDTO(user, newIngredient.getIngredientId(), RelatedEntity.INGREDIENT, ActivityType.INGREDIENT_SUGGESTED, description);
                    activityLogService.createActivityLog(activityLogRequestDTO);
                    return newIngredient;
                });
    }

    private String capitalizeIngredientName(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

    @Override
    public Page<Ingredient> findIngredientsByStatus(IngredientStatus ingredientStatus, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());
        return ingredientRepository.findByIngredientStatus(ingredientStatus, pageable);
    }

    @Override
    public Page<Ingredient> findIngredientsByType(IngredientType ingredientType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());
        return ingredientRepository.findByIngredientType(ingredientType, pageable);
    }

    @Override
    public Page<Ingredient> findAllIngredients(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());
        return ingredientRepository.findAll(pageable);
    }

    @Override
    public Page<Ingredient> findIngredientsByStatusAndType(IngredientStatus ingredientStatus, IngredientType ingredientType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());
        return ingredientRepository.findByIngredientStatusAndIngredientType(ingredientStatus, ingredientType, pageable);
    }

    @Override
    public List<IngredientAutocompleteDTO> getApprovedIngredientsForAutocomplete() {
        List<Ingredient> ingredients = ingredientRepository.findByIngredientStatus(IngredientStatus.APPROVED);
        List<IngredientAutocompleteDTO> autocompleteDTOList = new ArrayList<>();
        for (Ingredient ingredient : ingredients) {
            IngredientAutocompleteDTO ingredientAutocompleteDTO = new IngredientAutocompleteDTO(
                    ingredient.getIngredientName(),
                    ingredient.getUnitOfMeasure().toString()
            );
            autocompleteDTOList.add(ingredientAutocompleteDTO);
        }
        return autocompleteDTOList;
    }

    @Override
    public Ingredient findIngredientById(UUID id) {
        Optional<Ingredient> ingredient = ingredientRepository.findById(id);
        if (ingredient.isPresent()) {
            return ingredient.get();
        } else {
            throw new NoSuchElementException("Ingrediente con ID: " + id + " no existente");
        }
    }

    @Override
    public Ingredient updateIngredient(IngredientRequestDTO ingredientRequestDTO, Authentication authentication) {
        User user = userService.findUserByUsername(authentication.getName());
        validateIngredient(ingredientRequestDTO, user);
        Ingredient ingredient = findIngredientById(ingredientRequestDTO.getIngredientId());
        if (ingredient.getIngredientStatus().equals(IngredientStatus.PENDING_REVIEW) && ingredientRequestDTO.getIngredientStatus().equals(IngredientStatus.APPROVED)) {
            String description = "El usuario '" + user.getUsername() + "' ha aprobado el ingrediente '" + ingredient.getIngredientName() + "' (ID: " + ingredient.getIngredientId() + ")";
            ActivityLogRequestDTO activityLogRequestDTO = new ActivityLogRequestDTO(user, ingredient.getIngredientId(), RelatedEntity.INGREDIENT, ActivityType.INGREDIENT_APPROVED, description);
            activityLogService.createActivityLog(activityLogRequestDTO);
        } else {
            String description = "El usuario '" + user.getUsername() + "' ha actualizado el ingrediente '" + ingredient.getIngredientName() + "' (ID: " + ingredient.getIngredientId() + ")";
            ActivityLogRequestDTO activityLogRequestDTO = new ActivityLogRequestDTO(user, ingredient.getIngredientId(), RelatedEntity.INGREDIENT, ActivityType.INGREDIENT_UPDATE, description);
            activityLogService.createActivityLog(activityLogRequestDTO);
        }
        Ingredient updatedIngredient = ingredientRepository.save(ingredientMapper.toIngredient(ingredientRequestDTO));
        return updatedIngredient;
    }

    private void validateIngredient(IngredientRequestDTO ingredient, User user) {
        if (user == null) {
            throw new IllegalArgumentException("Tienes que estar autentificado para esta operación");
        }
        if (!user.getRole().equals(Role.ROLE_ADMIN)) {
            throw new IllegalArgumentException("No tienes los permisos necesarios para esta operación");
        }

        if (ingredient == null) {
            throw new IllegalArgumentException("El ingrediente no puede ser nulo");
        }

        Optional<Ingredient> existingIngredient = ingredientRepository.findById(ingredient.getIngredientId());
        if (!existingIngredient.isPresent()) {
            throw new NoSuchElementException("Ingrediente con ID " + ingredient.getIngredientId() + " no existe.");
        }

        if (ingredient.getIngredientName() == null || ingredient.getIngredientName().isEmpty()) {
            throw new IllegalArgumentException("El nombre del ingrediente no puede estar vacío.");
        }

        Optional<Ingredient> ingredientWithSameName = ingredientRepository.findByIngredientName(ingredient.getIngredientName());
        if (ingredientWithSameName.isPresent() && !ingredientWithSameName.get().getIngredientId().equals(ingredient.getIngredientId())) {
            throw new IllegalArgumentException("Ya existe otro ingrediente con el nombre " + ingredient.getIngredientName());
        }

        if (!EnumUtils.isValidEnum(IngredientType.class, ingredient.getIngredientType().name())) {
            throw new IllegalArgumentException("Tipo de ingrediente no válido.");
        }

        if (!EnumUtils.isValidEnum(UnitOfMeasure.class, ingredient.getUnitOfMeasure().name())) {
            throw new IllegalArgumentException("Unidad de medida no válida.");
        }

        if (ingredient.getCalories() <= 0) {
            throw new IllegalArgumentException("Las calorías deben ser mayores que cero.");
        }
    }
}
