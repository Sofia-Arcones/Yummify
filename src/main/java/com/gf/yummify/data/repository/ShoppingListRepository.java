package com.gf.yummify.data.repository;

import com.gf.yummify.data.entity.ShoppingList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ShoppingListRepository extends JpaRepository<ShoppingList, UUID> {
}
