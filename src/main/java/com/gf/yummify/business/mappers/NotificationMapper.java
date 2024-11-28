package com.gf.yummify.business.mappers;

import com.gf.yummify.data.entity.Notification;
import com.gf.yummify.presentation.dto.NotificationRequestDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    Notification toNotification(NotificationRequestDTO notificationRequestDTO);
}
