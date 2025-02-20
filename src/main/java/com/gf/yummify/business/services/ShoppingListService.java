package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.ShoppingList;
import com.gf.yummify.data.entity.User;
import com.gf.yummify.data.enums.ListStatus;
import com.gf.yummify.presentation.dto.ShoppingListItemRequestDTO;
import com.gf.yummify.presentation.dto.ShoppingListRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

public interface ShoppingListService {
    Page<ShoppingList> findLists(Authentication authentication, int page, int size);

    void saveList(ShoppingListRequestDTO shoppingListRequestDTO, Authentication authentication);

    Page<ShoppingList> findByStatus(Authentication authentication, ListStatus status, int page, int size);

    ShoppingList findListById(UUID id);

    ShoppingList updateListStatus(UUID id, UUID itemId, Boolean isPurchased, Boolean isArchived, Authentication authentication);

    String addIngredientToList(ShoppingListItemRequestDTO shoppingListItemRequestDTO, User user);

    List<ShoppingList> findListsByUser(Authentication authentication);
}
