package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.Challenge;
import com.gf.yummify.presentation.dto.ChallengeRequestDTO;
import com.gf.yummify.presentation.dto.ChallengeResponseDTO;

public interface ChallengeService {
    ChallengeResponseDTO addChallenge(ChallengeRequestDTO challengeRequestDTO);
}
