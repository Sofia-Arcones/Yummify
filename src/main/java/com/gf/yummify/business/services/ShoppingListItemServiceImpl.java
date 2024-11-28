package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.ShoppingList;
import com.gf.yummify.data.entity.ShoppingListItem;
import com.gf.yummify.data.entity.User;
import com.gf.yummify.data.enums.UnitOfMeasure;
import com.gf.yummify.data.repository.ShoppingListItemRepository;
import com.gf.yummify.presentation.dto.ShoppingListRequestDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ShoppingListItemServiceImpl implements ShoppingListItemService {
    private ShoppingListItemRepository shoppingListItemRepository;
    private IngredientService ingredientService;
    private ActivityLogService activityLogService;

    public ShoppingListItemServiceImpl(ShoppingListItemRepository shoppingListItemRepository, IngredientService ingredientService, ActivityLogService activityLogService) {
        this.shoppingListItemRepository = shoppingListItemRepository;
        this.ingredientService = ingredientService;
        this.activityLogService = activityLogService;
    }

    @Override
    public List<ShoppingListItem> generateShoppingListItems(ShoppingListRequestDTO shoppingListRequestDTO, ShoppingList shoppingList, User user) {
        if (shoppingListRequestDTO.getIngredients().size() != shoppingListRequestDTO.getQuantities().size() ||
                shoppingListRequestDTO.getIngredients().size() != shoppingListRequestDTO.getUnits().size()) {
            throw new IllegalArgumentException("Las listas de ingredientes, cantidades y unidades deben tener el mismo tamaño.");
        }

        List<ShoppingListItem> shoppingListItemList = new ArrayList<>();

        if (!shoppingListRequestDTO.getIngredients().isEmpty()) {
            for (int i = 0; i < shoppingListRequestDTO.getIngredients().size(); i++) {
                ShoppingListItem shoppingListItem = new ShoppingListItem();
                shoppingListItem.setShoppingList(shoppingList);
                shoppingListItem.setIngredient(ingredientService.findOrCreateIngredient(shoppingListRequestDTO.getIngredients().get(i), user));
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

    @Override
    public ShoppingListItem updateIsPurchased(UUID itemId, Boolean isPurchased) {
        ShoppingListItem shoppingListItem = findItemById(itemId);
        shoppingListItem.setIsPurchased(isPurchased);
        return shoppingListItemRepository.save(shoppingListItem);
    }

    @Override
    public ShoppingListItem findItemById(UUID id) {
        return shoppingListItemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("El item de la lista con id: " + id + " no existe"));
    }
}
