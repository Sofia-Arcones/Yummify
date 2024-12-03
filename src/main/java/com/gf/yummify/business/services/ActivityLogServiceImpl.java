package com.gf.yummify.business.services;

import com.gf.yummify.business.mappers.ActivityLogMapper;
import com.gf.yummify.data.entity.ActivityLog;
import com.gf.yummify.data.repository.ActivityLogRepository;
import com.gf.yummify.presentation.dto.ActivityLogRequestDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class ActivityLogServiceImpl implements ActivityLogService {
    private ActivityLogRepository activityLogRepository;
    private ActivityLogMapper activityLogMapper;
    private NotificationService notificationService;

    public ActivityLogServiceImpl(ActivityLogRepository activityLogRepository, ActivityLogMapper activityLogMapper, @Lazy NotificationService notificationService) {
        this.activityLogRepository = activityLogRepository;
        this.activityLogMapper = activityLogMapper;
        this.notificationService = notificationService;
    }

    @Override
    public void createActivityLog(ActivityLogRequestDTO activityLogRequestDTO) {
        ActivityLog activityLog = activityLogRepository.save(activityLogMapper.toActivityLog(activityLogRequestDTO));
        notificationService.sendNotifications(activityLog);
    }
}
