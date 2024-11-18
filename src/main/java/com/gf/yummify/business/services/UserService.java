package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.User;
import com.gf.yummify.presentation.dto.RegisterDTO;

public interface UserService {
    User createUser(RegisterDTO registerDTO);

    User findUserByUsername(String username);

    Boolean checkUserAuthentication(String loggedUsername, String profileUsername);
}
