package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.ActivityLog;
import com.gf.yummify.presentation.dto.NotificationResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface NotificationService {
    void sendNotifications(ActivityLog activityLog);

    Page<NotificationResponseDTO> getNotificationFromLastMonth(Authentication authentication, int page, int size);

    void markAsRead(UUID notificationId);

    long countUnreadNotifications(String username);
}
