package com.gf.yummify.data.repository;

import com.gf.yummify.data.entity.ChallengeParticipation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChallengeParticipationRepository extends JpaRepository<ChallengeParticipation, UUID> {
}
