package com.gf.yummify.presentation.dto;

import com.gf.yummify.data.entity.User;
import com.gf.yummify.data.enums.ActivityType;
import com.gf.yummify.data.enums.RelatedEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLogRequestDTO {
    private User user;
    private UUID relatedEntityId;
    private RelatedEntity relatedEntity;
    private ActivityType activityType;
    private String description;
}
