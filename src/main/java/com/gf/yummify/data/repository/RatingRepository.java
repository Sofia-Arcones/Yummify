package com.gf.yummify.data.repository;

import com.gf.yummify.data.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {
}
