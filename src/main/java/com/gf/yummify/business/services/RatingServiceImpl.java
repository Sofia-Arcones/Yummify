package com.gf.yummify.business.services;

import com.gf.yummify.business.mappers.RatingMapper;
import com.gf.yummify.data.entity.Comment;
import com.gf.yummify.data.entity.Rating;
import com.gf.yummify.data.entity.Recipe;
import com.gf.yummify.data.entity.User;
import com.gf.yummify.data.enums.ActivityType;
import com.gf.yummify.data.enums.RelatedEntity;
import com.gf.yummify.data.repository.RatingRepository;
import com.gf.yummify.presentation.dto.ActivityLogRequestDTO;
import com.gf.yummify.presentation.dto.RatingDTO;
import com.gf.yummify.presentation.dto.ReplyDTO;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class RatingServiceImpl implements RatingService {
    private RatingRepository ratingRepository;
    private RecipeService recipeService;
    private UserService userService;
    private CommentService commentService;
    private ActivityLogService activityLogService;
    private RatingMapper ratingMapper;

    public RatingServiceImpl(RatingRepository ratingRepository, @Lazy RecipeService recipeService, UserService userService, CommentService commentService, ActivityLogService activityLogService, RatingMapper ratingMapper) {
        this.ratingRepository = ratingRepository;
        this.recipeService = recipeService;
        this.userService = userService;
        this.commentService = commentService;
        this.activityLogService = activityLogService;
        this.ratingMapper = ratingMapper;
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
            Comment comment = commentService.addComment(rating, commentContent, user);
            List<Comment> commentList = rating.getComments();
            commentList.add(comment);
            rating.setComments(commentList);
        }
    }

    @Override
    public Rating findRateById(UUID id) {
        return ratingRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("El rate con id: " + id + " no existe"));
    }

    @Override
    public List<RatingDTO> mapToRatingDTO(Recipe recipe) {
        List<RatingDTO> ratingDTOList = new ArrayList<>();
        for (Rating rating : recipe.getRatings()) {
            RatingDTO ratingDTO = ratingMapper.toRatingDTO(rating);
            if (!rating.getComments().isEmpty()) {
                ratingDTO.setComment(rating.getComments().get(0).getComment());
                List<ReplyDTO> replyDTOList = new ArrayList<>();
                for (int i = 1; i < rating.getComments().size(); i++) {
                    Comment comment = rating.getComments().get(i);
                    ReplyDTO replyDTO = ratingMapper.toReplyDTO(comment);
                    replyDTOList.add(replyDTO);
                }
                ratingDTO.setReplies(replyDTOList);
            }
            ratingDTOList.add(ratingDTO);
        }
        return ratingDTOList;
    }
}
