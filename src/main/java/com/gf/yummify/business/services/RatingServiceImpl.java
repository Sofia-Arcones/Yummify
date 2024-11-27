package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.Comment;
import com.gf.yummify.data.entity.Rating;
import com.gf.yummify.data.entity.Recipe;
import com.gf.yummify.data.entity.User;
import com.gf.yummify.data.enums.ActivityType;
import com.gf.yummify.data.enums.RelatedEntity;
import com.gf.yummify.data.repository.RatingRepository;
import com.gf.yummify.presentation.dto.ActivityLogRequestDTO;
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
    private ActivityLogService activityLogService;

    public RatingServiceImpl(RatingRepository ratingRepository, RecipeService recipeService, UserService userService, CommentService commentService, ActivityLogService activityLogService) {
        this.ratingRepository = ratingRepository;
        this.recipeService = recipeService;
        this.userService = userService;
        this.commentService = commentService;
        this.activityLogService = activityLogService;
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
        String description = "El usuario '" + user.getUsername() + "' ha añadido un rating a la receta '" + recipe.getTitle() + "' (ID: " + recipe.getRecipeId() + ")";

        if (commentContent != null && !commentContent.trim().isEmpty()) {
            description = "El usuario '" + user.getUsername() + "' ha añadido un rating con comentario a la receta '" + recipe.getTitle() + "' (ID: " + recipe.getRecipeId() + ")";
            commentService.addComment(rating, commentContent, user);
        }
        ActivityLogRequestDTO activityLogRequestDTO = new ActivityLogRequestDTO(user, rating.getRateId(), RelatedEntity.RATING, ActivityType.RATING_ADDED, description);
        activityLogService.createActivityLog(activityLogRequestDTO);
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
                        .sorted(Comparator.comparing(Comment::getCommentDate))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Rating findRateById(UUID id) {
        return ratingRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("El rate con id: " + id + " no existe"));
    }

}
