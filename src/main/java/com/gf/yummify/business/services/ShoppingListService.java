package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.ShoppingList;
import com.gf.yummify.data.enums.ListStatus;
import com.gf.yummify.presentation.dto.ShoppingListRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

public interface ShoppingListService {
    Page<ShoppingList> findLists(Authentication authentication, int page, int size);

    void saveList(ShoppingListRequestDTO shoppingListRequestDTO, Authentication authentication);

    Page<ShoppingList> findByStatus(Authentication authentication, ListStatus status, int page, int size);
}
