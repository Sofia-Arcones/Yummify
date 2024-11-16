package com.gf.yummify.business.services;

import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface RatingService {
    void addRating(UUID recipeId, Double rate, String commentContent, Authentication authentication);
}
