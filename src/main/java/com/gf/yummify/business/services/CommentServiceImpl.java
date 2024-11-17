package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.Comment;
import com.gf.yummify.data.entity.Rating;
import com.gf.yummify.data.entity.User;
import com.gf.yummify.data.repository.CommentRepository;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {
    private CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
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

        return commentRepository.save(comment);
    }


}
