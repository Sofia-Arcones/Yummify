package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.User;
import com.gf.yummify.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userUsername = userRepository.findByUsername(username);
        if (userUsername.isPresent()) {
            return userUsername.get();
        }
        throw new UsernameNotFoundException("Usuario no encontrado");
    }
}
