package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.User;
import com.gf.yummify.data.enums.Role;
import com.gf.yummify.presentation.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User createUser(RegisterDTO registerDTO);

    User findUserByUsername(String username);

    Boolean checkUserAuthentication(String loggedUsername, String profileUsername);

    User findUserById(UUID userId);

    List<User> findAllUsers();

    UserResponseDTO findProfileUser(String username, int followers, int friends);

    List<User> findAllUsersByRole(Role role);

    ProfileUpdateDTO getProfileUpdateDTO(Authentication authentication);

    void updateProfile(Authentication authentication, ProfileUpdateRequestDTO profileUpdateRequestDTO);

    List<ShortUserDTO> searchUsers(String searchTerm);

    void requestVerification(Authentication authentication, String username);

    Page<UserResponseDTO> findUsersPage(String status, int page, int size);
}
