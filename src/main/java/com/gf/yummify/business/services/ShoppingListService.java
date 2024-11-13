package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.ShoppingList;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ShoppingListService {
    List<ShoppingList> findAllLists(Authentication authentication);
    void saveList(String title, Authentication authentication);
}
