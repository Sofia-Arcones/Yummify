package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.Ingredient;
import com.gf.yummify.data.enums.IngredientStatus;
import com.gf.yummify.data.repository.IngredientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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
    public Ingredient findOrCreateIngredient(String name) {
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

   /* public Ingredient approveIngredient(String ingredientName){
        Ingredient ingredient = findByIngredientName(ingredientName);
        ingredient.setIngredientType

    }
    */
}
