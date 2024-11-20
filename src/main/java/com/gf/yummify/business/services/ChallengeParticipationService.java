package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.Challenge;
import com.gf.yummify.presentation.dto.ChallengeParticipationResponseDTO;
import org.springframework.data.domain.Page;

public interface ChallengeParticipationService {
    Page<ChallengeParticipationResponseDTO> findChallengeParticipationPage(int size, int page, Challenge challenge);
}
