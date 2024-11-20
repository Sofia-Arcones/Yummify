package com.gf.yummify.data.repository;

import com.gf.yummify.data.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ChallengeRepository extends JpaRepository<Challenge, UUID> {
    Optional<Challenge> findChallengeByTitleAndIsFinished(String title, Boolean isFinished);
}
