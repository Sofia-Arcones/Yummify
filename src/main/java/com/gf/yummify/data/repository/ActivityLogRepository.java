package com.gf.yummify.data.repository;

import com.gf.yummify.data.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, UUID> {
}
