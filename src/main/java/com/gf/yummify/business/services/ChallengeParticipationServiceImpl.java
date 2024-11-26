package com.gf.yummify.business.services;

import com.gf.yummify.business.mappers.ChallengeParticipationMapper;
import com.gf.yummify.data.entity.Challenge;
import com.gf.yummify.data.entity.ChallengeParticipation;
import com.gf.yummify.data.entity.Recipe;
import com.gf.yummify.data.entity.User;
import com.gf.yummify.data.enums.ActivityType;
import com.gf.yummify.data.enums.RelatedEntity;
import com.gf.yummify.data.repository.ChallengeParticipationRepository;
import com.gf.yummify.presentation.dto.ActivityLogRequestDTO;
import com.gf.yummify.presentation.dto.ChallengeParticipationResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class ChallengeParticipationServiceImpl implements ChallengeParticipationService {
    private ChallengeParticipationRepository challengeParticipationRepository;
    private ChallengeParticipationMapper challengeParticipationMapper;
    private ActivityLogService activityLogService;

    public ChallengeParticipationServiceImpl(ChallengeParticipationRepository challengeParticipationRepository, ChallengeParticipationMapper challengeParticipationMapper, ActivityLogService activityLogService) {
        this.challengeParticipationRepository = challengeParticipationRepository;
        this.challengeParticipationMapper = challengeParticipationMapper;
        this.activityLogService = activityLogService;
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
        if (challengeParticipationRepository.findByChallengeAndUser(challenge, user).isPresent()) {
            throw new IllegalArgumentException("Ya tienes una participación en este desafío");
        }
        ChallengeParticipation challengeParticipation = new ChallengeParticipation();
        challengeParticipation.setChallenge(challenge);
        challengeParticipation.setRecipe(recipe);
        challengeParticipation.setUser(user);
        challengeParticipationRepository.save(challengeParticipation);
        String description = "El usuario '" + user.getUsername() + "' participó en el desafío '" + challenge.getTitle() + "' (ID: " + challenge.getChallengeId() + ") con su receta '" + recipe.getTitle() + "' (ID: " + recipe.getRecipeId() + ").";
        ActivityLogRequestDTO activityLogRequestDTO = new ActivityLogRequestDTO(user, challengeParticipation.getChallengeParticipationId(), RelatedEntity.CHALLENGE_PARTICIPATION, ActivityType.CHALLENGE_JOINED, description);
        activityLogService.createActivityLog(activityLogRequestDTO);
    }

    @Override
    public ChallengeParticipation findParticipationByChallengeAndUser(Challenge challenge, User user) {
        return challengeParticipationRepository.findByChallengeAndUser(challenge, user)
                .orElseThrow(() -> new NoSuchElementException("No existe ninguna participación con ese usuario."));
    }
}

