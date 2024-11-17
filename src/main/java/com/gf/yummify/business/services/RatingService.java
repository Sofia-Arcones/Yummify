package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.Rating;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface RatingService {
    void addRating(UUID recipeId, Double rate, String commentContent, Authentication authentication);

    Rating findRateById(UUID id);

    void addCommentToRating(UUID recipeId, String commentContent, Authentication authentication, UUID rateId);
}
