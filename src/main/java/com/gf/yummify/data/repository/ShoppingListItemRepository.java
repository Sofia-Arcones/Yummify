package com.gf.yummify.data.repository;

import com.gf.yummify.data.entity.ShoppingListItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingListItemRepository extends JpaRepository<ShoppingListItem, Long> {
}
