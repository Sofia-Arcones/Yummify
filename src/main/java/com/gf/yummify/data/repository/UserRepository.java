package com.gf.yummify.data.repository;

import com.gf.yummify.data.entity.User;
import com.gf.yummify.data.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role);

    List<User> findByUsernameContainingIgnoreCase(String searchTerm);
}
