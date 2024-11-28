package com.gf.yummify.data.repository;

import com.gf.yummify.data.entity.Notification;
import com.gf.yummify.data.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Page<Notification> findByUserAndCreationDateBetween(User user, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
