package com.gf.yummify.data.repository;

import com.gf.yummify.data.entity.ShoppingList;
import com.gf.yummify.data.entity.User;
import com.gf.yummify.data.enums.ListStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShoppingListRepository extends JpaRepository<ShoppingList, UUID> {
    Page<ShoppingList> findByUserAndListStatusIn(User user, List<ListStatus> listStatuses, Pageable pageable);

    Optional<ShoppingList> findByUserAndTitleAndListStatusIn(User user, String title, List<ListStatus> listStatuses);

    Page<ShoppingList> findByUserAndListStatus(User user, ListStatus listStatus, Pageable pageable);
}
