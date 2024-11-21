package com.gf.yummify.business.mappers;

import com.gf.yummify.data.entity.Challenge;
import com.gf.yummify.presentation.dto.ChallengeRequestDTO;
import com.gf.yummify.presentation.dto.ChallengeResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChallengeMapper {
    @Mapping(target = "isFinished", constant = "false")
    Challenge toChallenge(ChallengeRequestDTO challengeRequestDTO);
    @Mapping(target = "participations", ignore = true)
    ChallengeResponseDTO toChallengeResponseDTO(Challenge challenge);
}
