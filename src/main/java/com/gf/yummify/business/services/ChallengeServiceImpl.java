package com.gf.yummify.business.services;

import com.gf.yummify.business.mappers.ChallengeMapper;
import com.gf.yummify.data.entity.Challenge;
import com.gf.yummify.data.repository.ChallengeRepository;
import com.gf.yummify.presentation.dto.ChallengeRequestDTO;
import com.gf.yummify.presentation.dto.ChallengeResponseDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ChallengeServiceImpl implements ChallengeService {
    private ChallengeRepository challengeRepository;
    private ChallengeMapper challengeMapper;

    public ChallengeServiceImpl(ChallengeRepository challengeRepository, ChallengeMapper challengeMapper) {
        this.challengeRepository = challengeRepository;
        this.challengeMapper = challengeMapper;
    }

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
}
