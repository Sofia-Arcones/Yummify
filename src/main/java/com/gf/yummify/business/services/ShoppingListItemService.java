package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.ShoppingList;
import com.gf.yummify.data.entity.ShoppingListItem;
import com.gf.yummify.presentation.dto.ShoppingListRequestDTO;

import java.util.List;
import java.util.UUID;

public interface ShoppingListItemService {
    List<ShoppingListItem> generateShoppingListItems(ShoppingListRequestDTO shoppingListRequestDTO, ShoppingList shoppingList);

    ShoppingListItem updateIsPurchased(UUID itemId, Boolean isPurchased);

    ShoppingListItem findItemById(UUID id);
}
