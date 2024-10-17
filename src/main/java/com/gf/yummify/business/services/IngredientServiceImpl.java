package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.Ingredient;
import com.gf.yummify.data.enums.IngredientStatus;
import com.gf.yummify.data.enums.IngredientType;
import com.gf.yummify.data.repository.IngredientRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class IngredientServiceImpl implements IngredientService {
    private IngredientRepository ingredientRepository;

    public IngredientServiceImpl(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    public Ingredient findByIngredientName(String ingredientName) {
        Optional<Ingredient> ingredient = ingredientRepository.findByIngredientName(ingredientName);
        if (ingredient.isPresent()) {
            return ingredient.get();
        } else {
            throw new NoSuchElementException("No existe ese ingrediente");
        }
    }

    public Ingredient createIngredient(String ingredientName) {
        if (!ingredientRepository.existsByIngredientName(ingredientName)) {
            Ingredient newIngredient = new Ingredient();
            newIngredient.setIngredientName(ingredientName);
            newIngredient.setIngredientStatus(IngredientStatus.PENDING_REVIEW);
        }
        return null;
    }

   /* public Ingredient approveIngredient(String ingredientName){
        Ingredient ingredient = findByIngredientName(ingredientName);
        ingredient.setIngredientType

    }
    */
}
