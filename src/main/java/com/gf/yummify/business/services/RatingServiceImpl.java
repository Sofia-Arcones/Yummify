package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.Rating;
import com.gf.yummify.data.entity.Recipe;
import com.gf.yummify.data.entity.User;
import com.gf.yummify.data.repository.RatingRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RatingServiceImpl implements RatingService {
    private RatingRepository ratingRepository;
    private RecipeService recipeService;
    private UserService userService;
    private CommentService commentService;

    public RatingServiceImpl(RatingRepository ratingRepository, RecipeService recipeService, UserService userService, CommentService commentService) {
        this.ratingRepository = ratingRepository;
        this.recipeService = recipeService;
        this.userService = userService;
        this.commentService = commentService;
    }

    @Transactional
    @Override
    public void addRating(UUID recipeId, Double rate, String commentContent, Authentication authentication) {
        User user = userService.findUserByUsername(authentication.getName());
        Recipe recipe = recipeService.findRecipeById(recipeId);

        Rating rating = new Rating();
        rating.setRating(rate);
        rating.setUser(user);
        rating.setRecipe(recipe);
        rating = ratingRepository.save(rating);

        if (commentContent != null && !commentContent.trim().isEmpty()) {
            commentService.addComment(rating, commentContent, user);
        }
    }



}
