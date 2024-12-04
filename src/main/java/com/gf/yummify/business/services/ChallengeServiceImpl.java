package com.gf.yummify.business.services;

import com.gf.yummify.business.mappers.ChallengeMapper;
import com.gf.yummify.data.entity.Challenge;
import com.gf.yummify.data.entity.ChallengeParticipation;
import com.gf.yummify.data.entity.Recipe;
import com.gf.yummify.data.entity.User;
import com.gf.yummify.data.enums.ActivityType;
import com.gf.yummify.data.enums.RelatedEntity;
import com.gf.yummify.data.enums.Role;
import com.gf.yummify.data.repository.ChallengeRepository;
import com.gf.yummify.presentation.dto.ActivityLogRequestDTO;
import com.gf.yummify.presentation.dto.ChallengeRequestDTO;
import com.gf.yummify.presentation.dto.ChallengeResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ChallengeServiceImpl implements ChallengeService {
    private ChallengeRepository challengeRepository;
    private ChallengeMapper challengeMapper;
    private ChallengeParticipationService challengeParticipationService;
    private RecipeService recipeService;
    private UserService userService;
    private ActivityLogService activityLogService;

    public ChallengeServiceImpl(ChallengeRepository challengeRepository, ChallengeMapper challengeMapper, ChallengeParticipationService challengeParticipationService, RecipeService recipeService, UserService userService, ActivityLogService activityLogService) {
        this.challengeRepository = challengeRepository;
        this.challengeMapper = challengeMapper;
        this.challengeParticipationService = challengeParticipationService;
        this.recipeService = recipeService;
        this.userService = userService;
        this.activityLogService = activityLogService;
    }

    @Override
    public ChallengeResponseDTO addChallenge(ChallengeRequestDTO challengeRequestDTO, Authentication authentication) {
        User user = userService.findUserByUsername(authentication.getName());
        validateChallengeRequest(challengeRequestDTO, user);
        Challenge challenge = challengeMapper.toChallenge(challengeRequestDTO);
        challenge = challengeRepository.save(challenge);
        String description = "El usuario '" + user.getUsername() + "' ha creado el desafío '" + challenge.getTitle() + "' (ID: " + challenge.getChallengeId() + ")";
        ActivityLogRequestDTO activityLogRequestDTO = new ActivityLogRequestDTO(user, challenge.getChallengeId(), RelatedEntity.CHALLENGE, ActivityType.CHALLENGE_CREATED, description);
        activityLogService.createActivityLog(activityLogRequestDTO);
        return challengeMapper.toChallengeResponseDTO(challenge);
    }

    private void validateChallengeRequest(ChallengeRequestDTO challengeRequestDTO, User user) {
        if (user == null) {
            throw new IllegalArgumentException("Tienes que estar logeado para poder hacer esto.");
        }
        if (!user.getRole().equals(Role.ROLE_ADMIN)) {
            throw new IllegalArgumentException("Necesitas el rol de administrador para hacer esto.");
        }
        if (challengeRequestDTO.getTitle() == null || challengeRequestDTO.getTitle().isBlank()) {
            throw new IllegalArgumentException("El título del reto no puede estar vacío.");
        }

        if (challengeRequestDTO.getDescription() == null || challengeRequestDTO.getDescription().isBlank()) {
            throw new IllegalArgumentException("La descripción del reto no puede estar vacía.");
        }
        if (challengeRequestDTO.getWinnerQuantity() == null || challengeRequestDTO.getWinnerQuantity() <= 0) {
            throw new IllegalArgumentException("La cantidad de ganadores debe ser un número positivo.");
        }
        if (challengeRequestDTO.getStartDate() == null) {
            throw new IllegalArgumentException("No has introducido fecha de inicio.");
        }
        if (challengeRequestDTO.getEndDate() == null) {
            throw new IllegalArgumentException("No has introducido fecha de fin.");
        }
        if (challengeRequestDTO.getStartDate().isAfter(challengeRequestDTO.getEndDate())) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha de finalización.");
        }

        if (challengeRequestDTO.getStartDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser anterior a la fecha actual.");
        }
        if (challengeRequestDTO.getReward() == null || challengeRequestDTO.getReward().isBlank()) {
            throw new IllegalArgumentException("La recompensa del reto no puede estar vacía.");
        }
        boolean exists = challengeRepository.findChallengeByTitleAndIsFinished(challengeRequestDTO.getTitle(), false).isPresent();
        if (exists) {
            throw new IllegalArgumentException("Ya existe un desafío activo con el título: " + challengeRequestDTO.getTitle());
        }
    }

    @Override
    public Page<Challenge> findChallenges(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("endDate").descending());
        return challengeRepository.findAll(pageable);

    }

    @Override
    public Page<Challenge> findChallengesByIsFinished(int page, int size, Boolean isFinished) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("endDate").descending());
        return challengeRepository.findByIsFinished(isFinished, pageable);
    }

    @Override
    public Page<Challenge> findEndingSoonChallenges(int page, int size) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(2);
        Pageable pageable = PageRequest.of(page, size, Sort.by("endDate").descending());
        return challengeRepository.findByEndDateBetween(startDate, endDate, pageable);
    }

    @Override
    public Challenge findChallengeById(UUID id) {
        return challengeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("El desafío con id: " + id + " no existe"));
    }


    @Override
    public ChallengeResponseDTO findChallengeWithPageParticipations(UUID challengeId, int size, int page) {
        Challenge challenge = findChallengeById(challengeId);
        ChallengeResponseDTO challengeResponseDTO = challengeMapper.toChallengeResponseDTO(challenge);
        challengeResponseDTO.setParticipations(challengeParticipationService.findChallengeParticipationPage(size, page, challenge));
        return challengeResponseDTO;
    }

    @Override
    public void addParticipationToChallenge(UUID challengeId, UUID recipeId, Authentication authentication) {
        User user = userService.findUserByUsername(authentication.getName());
        Recipe recipe = recipeService.findRecipeById(recipeId);
        Challenge challenge = findChallengeById(challengeId);
        challengeParticipationService.addChallengeParticipation(challenge, recipe, user);
    }

    @Override
    public void setWinners(List<UUID> participationsIds, UUID challengeId, Authentication authentication) {
        Challenge challenge = findChallengeById(challengeId);
        long currentWinnerCount = challenge.getParticipations().stream()
                .filter(ChallengeParticipation::getIsWinner)
                .count();
        long remainingWinnerSpots = challenge.getWinnerQuantity() - currentWinnerCount;
        if (participationsIds.size() > remainingWinnerSpots) {
            throw new IllegalArgumentException("Has seleccionado más ganadores de los permitidos por el desafío");
        }
        challengeParticipationService.findAndSetWinners(participationsIds);
        long finalWinnerCount = challenge.getParticipations().stream()
                .filter(ChallengeParticipation::getIsWinner)
                .count();
        if (finalWinnerCount == challenge.getWinnerQuantity()) {
            challenge.setIsFinished(true);
            challengeRepository.save(challenge);
            User user = userService.findUserByUsername(authentication.getName());
            String description = "El administrador '" + user.getUsername() + "' ha dado por finalizado el reto '" + challenge.getTitle() + "' (ID: " + challenge.getChallengeId() + ").";
            ActivityLogRequestDTO activityLogRequestDTO = new ActivityLogRequestDTO(user, challenge.getChallengeId(), RelatedEntity.CHALLENGE, ActivityType.CHALLENGE_FINISHED, description);
            activityLogService.createActivityLog(activityLogRequestDTO);
        }
    }

    @Override
    public List<Challenge> findActiveChallenges() {
        return challengeRepository.findByIsFinished(false);
    }

    @Override
    public List<Challenge> findEndingSoonChallenges() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(2);
        return challengeRepository.findByEndDateBetween(startDate, endDate);
    }
}
