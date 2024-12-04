package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.Comment;
import com.gf.yummify.data.entity.Rating;
import com.gf.yummify.data.entity.User;
import com.gf.yummify.data.enums.ActivityType;
import com.gf.yummify.data.enums.RelatedEntity;
import com.gf.yummify.data.repository.CommentRepository;
import com.gf.yummify.presentation.dto.ActivityLogRequestDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class CommentServiceImpl implements CommentService {
    private CommentRepository commentRepository;
    private ActivityLogService activityLogService;

    public CommentServiceImpl(CommentRepository commentRepository, ActivityLogService activityLogService) {
        this.commentRepository = commentRepository;
        this.activityLogService = activityLogService;
    }

    @Override
    public Comment addComment(Rating rating, String commentContent, User user) {
        if (rating == null) {
            throw new IllegalArgumentException("La valoración no puede ser nula.");
        }
        if (commentContent == null || commentContent.trim().isEmpty()) {
            throw new IllegalArgumentException("El contenido del comentario no puede estar vacío.");
        }
        if (user == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo.");
        }

        Comment comment = new Comment();
        comment.setComment(commentContent);
        comment.setUser(user);
        comment.setRate(rating);
        comment = commentRepository.save(comment);

        String description = "El usuario '" + user.getUsername() + "' ha añadido un comentario en el rate (ID: " + rating.getRateId() + ")";
        ActivityLogRequestDTO activityLogRequestDTO = new ActivityLogRequestDTO(user, comment.getCommentId(), RelatedEntity.COMMENT, ActivityType.COMMENT_ADDED, description);
        activityLogService.createActivityLog(activityLogRequestDTO);

        return comment;
    }

    @Override
    public Comment findCommentById(UUID commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("El comentario con id: " + commentId + " no existe"));
    }

    @Override
    public List<Comment> findAllComments() {
        return commentRepository.findAll();
    }
}
