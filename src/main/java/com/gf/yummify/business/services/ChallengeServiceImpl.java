package com.gf.yummify.business.services;

import com.gf.yummify.business.mappers.ChallengeMapper;
import com.gf.yummify.data.entity.Challenge;
import com.gf.yummify.data.repository.ChallengeRepository;
import com.gf.yummify.presentation.dto.ChallengeRequestDTO;
import com.gf.yummify.presentation.dto.ChallengeResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ChallengeServiceImpl implements ChallengeService {
    private ChallengeRepository challengeRepository;
    private ChallengeMapper challengeMapper;
    private ChallengeParticipationService challengeParticipationService;

    public ChallengeServiceImpl(ChallengeRepository challengeRepository, ChallengeMapper challengeMapper, ChallengeParticipationService challengeParticipationService) {
        this.challengeRepository = challengeRepository;
        this.challengeMapper = challengeMapper;
        this.challengeParticipationService = challengeParticipationService;
    }

    @Override
    public ChallengeResponseDTO addChallenge(ChallengeRequestDTO challengeRequestDTO) {
        validateChallengeRequest(challengeRequestDTO);
        Challenge challenge = challengeMapper.toChallenge(challengeRequestDTO);
        challenge = challengeRepository.save(challenge);
        return challengeMapper.toChallengeResponseDTO(challenge);
    }

    private void validateChallengeRequest(ChallengeRequestDTO challengeRequestDTO) {
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
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());
        return challengeRepository.findAll(pageable);

    }

    @Override
    public Page<Challenge> findChallengesByIsFinished(int page, int size, Boolean isFinished) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());
        return challengeRepository.findByIsFinished(isFinished, pageable);
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
}
