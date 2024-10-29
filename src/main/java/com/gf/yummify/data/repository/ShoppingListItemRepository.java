package com.gf.yummify.data.repository;

import com.gf.yummify.data.entity.ShoppingListItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ShoppingListItemRepository extends JpaRepository<ShoppingListItem, UUID> {
}
