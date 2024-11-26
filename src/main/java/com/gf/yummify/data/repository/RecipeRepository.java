package com.gf.yummify.data.repository;

import com.gf.yummify.data.entity.Recipe;
import com.gf.yummify.data.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface RecipeRepository extends JpaRepository<Recipe, UUID>, JpaSpecificationExecutor<Recipe> {
    List<Recipe> findByUser(User user);

    @Override
    Page<Recipe> findAll(Specification<Recipe> specification, Pageable pageable);

    Page<Recipe> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
