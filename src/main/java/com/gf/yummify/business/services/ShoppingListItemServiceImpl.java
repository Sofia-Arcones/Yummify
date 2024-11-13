package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.ShoppingList;
import com.gf.yummify.data.entity.ShoppingListItem;
import com.gf.yummify.data.enums.UnitOfMeasure;
import com.gf.yummify.data.repository.ShoppingListItemRepository;
import com.gf.yummify.presentation.dto.ShoppingListRequestDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShoppingListItemServiceImpl implements ShoppingListItemService {
    private ShoppingListItemRepository shoppingListItemRepository;
    private IngredientService ingredientService;

    public ShoppingListItemServiceImpl(ShoppingListItemRepository shoppingListItemRepository, IngredientService ingredientService) {
        this.shoppingListItemRepository = shoppingListItemRepository;
        this.ingredientService = ingredientService;
    }

    @Override
    public List<ShoppingListItem> generateShoppingListItems(ShoppingListRequestDTO shoppingListRequestDTO, ShoppingList shoppingList) {
        if (shoppingListRequestDTO.getIngredients().size() != shoppingListRequestDTO.getQuantities().size() ||
                shoppingListRequestDTO.getIngredients().size() != shoppingListRequestDTO.getUnits().size()) {
            throw new IllegalArgumentException("Las listas de ingredientes, cantidades y unidades deben tener el mismo tamaño.");
        }

        List<ShoppingListItem> shoppingListItemList = new ArrayList<>();

        if (!shoppingListRequestDTO.getIngredients().isEmpty()) {
            for (int i = 0; i < shoppingListRequestDTO.getIngredients().size(); i++) {
                ShoppingListItem shoppingListItem = new ShoppingListItem();
                shoppingListItem.setShoppingList(shoppingList); // Asocia el ShoppingList existente
                shoppingListItem.setIngredient(ingredientService.findOrCreateIngredient(shoppingListRequestDTO.getIngredients().get(i)));
                shoppingListItem.setQuantity(shoppingListRequestDTO.getQuantities().get(i));
                shoppingListItem.setIsPurchased(false);

                try {
                    shoppingListItem.setUnitOfMeasure(UnitOfMeasure.valueOf(shoppingListRequestDTO.getUnits().get(i)));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Unidad de medida no válida: " + shoppingListRequestDTO.getUnits().get(i), e);
                }

                shoppingListItemList.add(shoppingListItem);
            }
        }
        return shoppingListItemList;
    }

}
