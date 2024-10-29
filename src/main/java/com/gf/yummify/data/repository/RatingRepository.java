package com.gf.yummify.data.repository;

import com.gf.yummify.data.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RatingRepository extends JpaRepository<Rating, UUID> {
}
