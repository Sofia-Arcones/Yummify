package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.Rating;
import com.gf.yummify.data.entity.Recipe;
import com.gf.yummify.presentation.dto.RatingDTO;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

public interface RatingService {
    void addRating(UUID recipeId, Double rate, String commentContent, Authentication authentication);

    Rating findRateById(UUID id);

    void addCommentToRating(UUID recipeId, String commentContent, Authentication authentication, UUID rateId);

    List<RatingDTO> mapToRatingDTO(Recipe recipe);
}
