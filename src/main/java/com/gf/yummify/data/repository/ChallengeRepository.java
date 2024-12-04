package com.gf.yummify.data.repository;

import com.gf.yummify.data.entity.Challenge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChallengeRepository extends JpaRepository<Challenge, UUID> {
    Optional<Challenge> findChallengeByTitleAndIsFinished(String title, Boolean isFinished);

    Page<Challenge> findByIsFinished(Boolean isFinished, Pageable pageable);

    List<Challenge> findByIsFinished(Boolean isFinished);

    List<Challenge> findByEndDateBetween(LocalDate startDate, LocalDate endDate);


}
