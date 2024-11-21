package com.gf.yummify.business.mappers;

import com.gf.yummify.data.entity.ChallengeParticipation;
import com.gf.yummify.presentation.dto.ChallengeParticipationResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChallengeParticipationMapper {
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "recipe.recipeId", target = "recipeId")
    @Mapping(source = "isWinner", target = "isWinner")
    @Mapping(source = "formattedParticipationDate", target = "formattedParticipationDate")
    ChallengeParticipationResponseDTO toChallengeParticipationResponseDto(ChallengeParticipation challengeParticipation);

}
