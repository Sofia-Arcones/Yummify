package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.User;
import com.gf.yummify.data.enums.Role;
import com.gf.yummify.presentation.dto.RegisterDTO;
import com.gf.yummify.presentation.dto.UserResponseDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User createUser(RegisterDTO registerDTO);

    User findUserByUsername(String username);

    Boolean checkUserAuthentication(String loggedUsername, String profileUsername);

    User findUserById(UUID userId);

    UserResponseDTO findProfileUser(String username, int followers, int friends);

    List<User> findAllUsersByRole(Role role);
}
