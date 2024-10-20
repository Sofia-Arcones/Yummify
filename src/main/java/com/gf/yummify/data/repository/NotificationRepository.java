package com.gf.yummify.data.repository;

import com.gf.yummify.data.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
