package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.ShoppingList;
import com.gf.yummify.presentation.dto.ShoppingListRequestDTO;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ShoppingListService {
    List<ShoppingList> findAllLists(Authentication authentication);
    void saveList(ShoppingListRequestDTO shoppingListRequestDTO, Authentication authentication);
}
