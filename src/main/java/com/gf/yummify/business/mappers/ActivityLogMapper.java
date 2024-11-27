package com.gf.yummify.business.mappers;

import com.gf.yummify.data.entity.ActivityLog;
import com.gf.yummify.presentation.dto.ActivityLogRequestDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ActivityLogMapper {
    ActivityLog toActivityLog(ActivityLogRequestDTO activityLogRequestDTO);

}
