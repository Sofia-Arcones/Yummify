package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.Ingredient;
import com.gf.yummify.data.entity.User;
import com.gf.yummify.data.enums.IngredientStatus;
import com.gf.yummify.data.enums.IngredientType;
import com.gf.yummify.data.enums.UnitOfMeasure;
import com.gf.yummify.data.repository.IngredientRepository;
import com.gf.yummify.presentation.dto.IngredientAutocompleteDTO;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class IngredientServiceImpl implements IngredientService {
    private IngredientRepository ingredientRepository;

    public IngredientServiceImpl(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
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
                    return ingredientRepository.save(newIngredient);
                });
    }

    private String capitalizeIngredientName(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

    @Override
    public List<Ingredient> findIngredientsByStatus(IngredientStatus ingredientStatus) {
        return ingredientRepository.findByIngredientStatus(ingredientStatus);
    }
    @Override
    public List<Ingredient> findIngredientsByType(IngredientType ingredientType){
        return ingredientRepository.findByIngredientType(ingredientType);
    }
    @Override
    public String deleteIngredient(UUID id){
        ingredientRepository.deleteById(id);
        return "Ingrediente eliminado correctamente.";
    }
    @Override
    public List<Ingredient> findIngredientsByStatusAndType(IngredientStatus ingredientStatus, IngredientType ingredientType){
        return ingredientRepository.findByIngredientStatusAndIngredientType(ingredientStatus, ingredientType);
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
    public Ingredient updateIngredient(Ingredient ingredient) {
        if (ingredient == null) {
            throw new IllegalArgumentException("El ingrediente no puede ser null");
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

        return ingredientRepository.save(ingredient);
    }
}
