package com.gf.yummify.business.services;

import com.gf.yummify.business.mappers.UserMapper;
import com.gf.yummify.data.entity.User;
import com.gf.yummify.data.enums.ActivityType;
import com.gf.yummify.data.enums.RelatedEntity;
import com.gf.yummify.data.repository.UserRepository;
import com.gf.yummify.presentation.dto.ActivityLogRequestDTO;
import com.gf.yummify.presentation.dto.RegisterDTO;
import com.gf.yummify.presentation.dto.UserResponseDTO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private UserMapper userMapper;
    private ActivityLogService activityLogService;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, ActivityLogService activityLogService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.activityLogService = activityLogService;
    }

    @Override
    public User createUser(RegisterDTO registerDTO) {
        if (userRepository.findByUsername(registerDTO.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con ese nombre");
        }
        if (userRepository.findByEmail(registerDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con ese correo");
        }
        if (!registerDTO.getPassword().matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,16}$")) {
            throw new IllegalArgumentException("La contraseña debe tener entre 8 y 16 caracteres, al menos una letra mayúscula, una letra minúscula, un número y ningún carácter especial.");
        }
        User user = userMapper.toUser(registerDTO);
        String encodedPassword = new BCryptPasswordEncoder().encode(user.getPassword());
        user.setPassword(encodedPassword);
        user = userRepository.save(user);
        String description = "El usuario con username '" + user.getUsername() + "' (ID: " + user.getUserId() + ") ha sido registrado correctamente";
        ActivityLogRequestDTO activityLogRequestDTO = new ActivityLogRequestDTO(user, user.getUserId(), RelatedEntity.USER, ActivityType.USER_REGISTER, description);
        activityLogService.createActivityLog(activityLogRequestDTO);
        return user;
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new NoSuchElementException("Usuario no encontrado con usuario: " + username));
    }

    @Override
    public Boolean checkUserAuthentication(String loggedUsername, String profileUsername) {
        User loggedUser = findUserByUsername(loggedUsername);
        User profileUser = findUserByUsername(profileUsername);
        return profileUser.getUserId().equals(loggedUser.getUserId());
    }

    @Override
    public UserResponseDTO findProfileUser(String username, int followers, int friends) {
        User user = findUserByUsername(username);
        UserResponseDTO userResponseDTO = userMapper.toUserResponseDTO(user);
        userResponseDTO.setFollowers(followers);
        userResponseDTO.setFriends(friends);
        return userResponseDTO;
    }

    @Override
    public User findUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("El usuario con id: " + userId + " no existe"));
    }
}
