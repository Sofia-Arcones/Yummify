package com.gf.yummify.data.repository;

import com.gf.yummify.data.entity.Challenge;
import com.gf.yummify.data.entity.ChallengeParticipation;
import com.gf.yummify.data.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ChallengeParticipationRepository extends JpaRepository<ChallengeParticipation, UUID> {
    Page<ChallengeParticipation> findByChallenge(Challenge challenge, Pageable pageable);
    Optional<ChallengeParticipation> findByChallengeAndUser(Challenge challenge, User user);

}
