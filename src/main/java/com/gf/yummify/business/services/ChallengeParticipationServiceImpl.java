package com.gf.yummify.business.services;

import com.gf.yummify.business.mappers.ChallengeParticipationMapper;
import com.gf.yummify.data.entity.Challenge;
import com.gf.yummify.data.entity.ChallengeParticipation;
import com.gf.yummify.data.entity.Recipe;
import com.gf.yummify.data.entity.User;
import com.gf.yummify.data.repository.ChallengeParticipationRepository;
import com.gf.yummify.presentation.dto.ChallengeParticipationResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ChallengeParticipationServiceImpl implements ChallengeParticipationService {
    private ChallengeParticipationRepository challengeParticipationRepository;
    private ChallengeParticipationMapper challengeParticipationMapper;

    public ChallengeParticipationServiceImpl(ChallengeParticipationRepository challengeParticipationRepository, ChallengeParticipationMapper challengeParticipationMapper) {
        this.challengeParticipationRepository = challengeParticipationRepository;
        this.challengeParticipationMapper = challengeParticipationMapper;
    }

    @Override
    public Page<ChallengeParticipationResponseDTO> findChallengeParticipationPage(int size, int page, Challenge challenge) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("participationDate").descending());
        Page<ChallengeParticipation> challengeParticipationPage = challengeParticipationRepository.findByChallenge(challenge, pageable);
        Page<ChallengeParticipationResponseDTO> challengeParticipationResponseDTOS = challengeParticipationPage.map(challengeParticipationMapper::toChallengeParticipationResponseDto);
        return challengeParticipationResponseDTOS;
    }

    @Override
    public void addChallengeParticipation(Challenge challenge, Recipe recipe, User user) {
        ChallengeParticipation challengeParticipation = new ChallengeParticipation();
        challengeParticipation.setChallenge(challenge);
        challengeParticipation.setRecipe(recipe);
        challengeParticipation.setUser(user);
        challengeParticipationRepository.save(challengeParticipation);
    }
}
