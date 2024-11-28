package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.ActivityLog;

public interface NotificationService {
    void sendNotifications(ActivityLog activityLog);
}
