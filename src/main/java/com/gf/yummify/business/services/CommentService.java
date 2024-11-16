package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.Comment;
import com.gf.yummify.data.entity.Rating;
import com.gf.yummify.data.entity.User;

public interface CommentService {
    Comment addComment(Rating rating, String commentContent, User user);
}
