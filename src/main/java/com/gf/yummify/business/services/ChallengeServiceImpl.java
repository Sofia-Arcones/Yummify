package com.gf.yummify.business.services;

import com.gf.yummify.data.repository.ChallengeRepository;
import org.springframework.stereotype.Service;

@Service
public class ChallengeServiceImpl implements ChallengeService{
    private ChallengeRepository challengeRepository;

    public ChallengeServiceImpl(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }
}
