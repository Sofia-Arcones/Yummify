package com.gf.yummify.business.mappers;

import com.gf.yummify.data.entity.User;
import com.gf.yummify.presentation.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "bio", ignore = true)
    @Mapping(target = "gender", ignore = true)
    @Mapping(target = "birthday", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "verificationStatus", constant = "NO_VERIFICADO")
    @Mapping(target = "registrationDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "lastModification", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "role", constant = "ROLE_USER")
    User toUser(RegisterDTO registerDTO);

    RegisterDTO toRegisterDTO(User user);

    @Mapping(target = "followers", ignore = true)
    @Mapping(target = "friends", ignore = true)
    @Mapping(target = "recipes", source = "user.recipes")
    UserResponseDTO toUserResponseDTO(User user);

    @Mapping(target = "formattedBirthday", ignore = true)
    ProfileUpdateDTO toProfileUpdateDTO(User user);


    @Mapping(target = "birthday", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    User toUser(ProfileUpdateRequestDTO profileUpdateRequestDTO);

    @Mapping(target = "avatar", source = "user.avatar")
    @Mapping(target = "username", source = "user.username")
    ShortUserDTO toShortUserDTO(User user);
}

