package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.Challenge;
import com.gf.yummify.presentation.dto.ChallengeRequestDTO;
import com.gf.yummify.presentation.dto.ChallengeResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

public interface ChallengeService {
    ChallengeResponseDTO addChallenge(ChallengeRequestDTO challengeRequestDTO, Authentication authentication);

    Page<Challenge> findChallenges(int page, int size);

    Page<Challenge> findChallengesByIsFinished(int page, int size, Boolean isFinished);

    Challenge findChallengeById(UUID id);

    ChallengeResponseDTO findChallengeWithPageParticipations(UUID challengeId, int size, int page);

    void addParticipationToChallenge(UUID challengeId, UUID recipeId, Authentication authentication);

    void setWinners(List<UUID> participationsIds, UUID challengeId);

}
