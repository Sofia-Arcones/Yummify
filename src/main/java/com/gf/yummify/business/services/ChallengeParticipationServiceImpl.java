package com.gf.yummify.business.services;

import com.gf.yummify.data.repository.ChallengeParticipationRepository;
import org.springframework.stereotype.Service;

@Service
public class ChallengeParticipationServiceImpl implements ChallengeParticipationService{
    private ChallengeParticipationRepository challengeParticipationRepository;

    public ChallengeParticipationServiceImpl(ChallengeParticipationRepository challengeParticipationRepository) {
        this.challengeParticipationRepository = challengeParticipationRepository;
    }
}
