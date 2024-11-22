package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.Comment;
import com.gf.yummify.data.entity.Rating;
import com.gf.yummify.data.entity.Recipe;
import com.gf.yummify.data.entity.User;
import com.gf.yummify.data.repository.RatingRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Override
    public void addCommentToRating(UUID recipeId, String commentContent, Authentication authentication, UUID rateId) {
        User user = userService.findUserByUsername(authentication.getName());
        Recipe recipe = recipeService.findRecipeById(recipeId);
        Rating rating = findRateById(rateId);
        if (commentContent != null && !commentContent.trim().isEmpty()) {
            commentService.addComment(rating, commentContent, user);
        }
        rating.setComments(
                rating.getComments().stream()
                        .sorted(Comparator.comparing(Comment::getCommentDate)) // Ordenar por fecha
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Rating findRateById(UUID id) {
        return ratingRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("El rate con id: " + id + " no existe"));
    }

}
