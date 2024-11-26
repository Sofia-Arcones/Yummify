package com.gf.yummify.business.services;

import com.gf.yummify.presentation.dto.ActivityLogRequestDTO;

public interface ActivityLogService {
    void createActivityLog(ActivityLogRequestDTO activityLogRequestDTO);
}
