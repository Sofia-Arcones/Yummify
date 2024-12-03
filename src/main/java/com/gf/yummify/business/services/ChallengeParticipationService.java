package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.Challenge;
import com.gf.yummify.data.entity.ChallengeParticipation;
import com.gf.yummify.data.entity.Recipe;
import com.gf.yummify.data.entity.User;
import com.gf.yummify.presentation.dto.ChallengeParticipationResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface ChallengeParticipationService {
    Page<ChallengeParticipationResponseDTO> findChallengeParticipationPage(int size, int page, Challenge challenge);

    void addChallengeParticipation(Challenge challenge, Recipe recipe, User user);

    ChallengeParticipation findParticipationByChallengeAndUser(Challenge challenge, User user);

    void findAndSetWinners(List<UUID> winnersID);

    ChallengeParticipation findChallengeParticipationById(UUID challengeParticipationId);
}
