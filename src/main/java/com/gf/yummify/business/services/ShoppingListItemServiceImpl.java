package com.gf.yummify.business.services;

import com.gf.yummify.data.repository.ShoppingListItemRepository;
import org.springframework.stereotype.Service;

@Service
public class ShoppingListItemServiceImpl implements ShoppingListItemService{
    private ShoppingListItemRepository shoppingListItemRepository;

    public ShoppingListItemServiceImpl(ShoppingListItemRepository shoppingListItemRepository) {
        this.shoppingListItemRepository = shoppingListItemRepository;
    }
}
