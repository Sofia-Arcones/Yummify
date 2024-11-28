package com.gf.yummify.presentation.dto;

import com.gf.yummify.data.entity.ActivityLog;
import com.gf.yummify.data.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequestDTO {
    private User user;
    private String content;
    private Boolean isRead;
    private ActivityLog activityLog;
}
