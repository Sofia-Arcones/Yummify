package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.User;

public interface UserService {
    User createUser(String username, String email, String password);
    User findUserByUsername(String username);
}
