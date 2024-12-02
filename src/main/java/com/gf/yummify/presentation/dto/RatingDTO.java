package com.gf.yummify.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingDTO {
    private UUID ratingId;
    private String userAvatar;
    private String username;
    private String comment;
    private String formattedDate;
    private Double ratingValue;
    private List<ReplyDTO> replies;
}
