package com.gf.yummify.business.services;

import com.gf.yummify.business.mappers.ActivityLogMapper;
import com.gf.yummify.data.repository.ActivityLogRepository;
import com.gf.yummify.presentation.dto.ActivityLogRequestDTO;
import org.springframework.stereotype.Service;

@Service
public class ActivityLogServiceImpl implements ActivityLogService {
    private ActivityLogRepository activityLogRepository;
    private ActivityLogMapper activityLogMapper;

    public ActivityLogServiceImpl(ActivityLogRepository activityLogRepository, ActivityLogMapper activityLogMapper) {
        this.activityLogRepository = activityLogRepository;
        this.activityLogMapper = activityLogMapper;
    }

    @Override
    public void createActivityLog(ActivityLogRequestDTO activityLogRequestDTO) {
        activityLogRepository.save(activityLogMapper.toActivityLog(activityLogRequestDTO));
    }

}
