package com.gf.yummify.business.mappers;

import com.gf.yummify.data.entity.Comment;
import com.gf.yummify.data.entity.Rating;
import com.gf.yummify.presentation.dto.RatingDTO;
import com.gf.yummify.presentation.dto.ReplyDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RatingMapper {
    @Mapping(target = "ratingId", source = "rating.rateId")
    @Mapping(target = "userAvatar", source = "rating.user.avatar")
    @Mapping(target = "username", source = "rating.user.username")
    @Mapping(target = "formattedDate", source = "rating.formattedCreationDate")
    @Mapping(target = "ratingValue", source = "rating.rating")
    @Mapping(target = "comment", ignore = true)
    @Mapping(target = "replies", ignore = true)
    RatingDTO toRatingDTO(Rating rating);

    @Mapping(target = "userAvatar", source = "comment.user.avatar")
    @Mapping(target = "username", source = "comment.user.username")
    @Mapping(target = "formattedDate", source = "comment.formattedCommentDate")
    @Mapping(target = "comment", source = "comment.comment")
    ReplyDTO toReplyDTO(Comment comment);
}
