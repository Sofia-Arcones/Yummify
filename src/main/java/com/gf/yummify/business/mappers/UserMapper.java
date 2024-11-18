package com.gf.yummify.business.mappers;

import com.gf.yummify.data.entity.User;
import com.gf.yummify.presentation.dto.RegisterDTO;
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
}

