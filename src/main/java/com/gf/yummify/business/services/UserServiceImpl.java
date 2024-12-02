package com.gf.yummify.business.services;

import com.gf.yummify.business.mappers.UserMapper;
import com.gf.yummify.data.entity.User;
import com.gf.yummify.data.enums.ActivityType;
import com.gf.yummify.data.enums.Gender;
import com.gf.yummify.data.enums.RelatedEntity;
import com.gf.yummify.data.enums.Role;
import com.gf.yummify.data.repository.UserRepository;
import com.gf.yummify.presentation.dto.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private UserMapper userMapper;
    private ActivityLogService activityLogService;

    private static final String UPLOAD_DIR = "src/main/resources/static/images/uploads/avatars";
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList("image/jpeg", "image/png");

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

    @Override
    public List<User> findAllUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    @Override
    public ProfileUpdateDTO getProfileUpdateDTO(Authentication authentication) {
        User user = findUserByUsername(authentication.getName());
        ProfileUpdateDTO profileUpdateDTO = userMapper.toProfileUpdateDTO(user);
        if (user.getBirthday() != null) {
            String formattedBirthday = user.getBirthday().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            profileUpdateDTO.setFormattedBirthday(formattedBirthday);
        }
        return profileUpdateDTO;
    }

    @Override
    public void updateProfile(Authentication authentication, ProfileUpdateRequestDTO profileUpdateRequestDTO) {
        if (authentication == null) {
            throw new IllegalArgumentException("Tienes que estar autentificado para esta operación");
        }
        System.out.println("Fecha: " + profileUpdateRequestDTO.getFormattedBirthday());
        User user = findUserByUsername(authentication.getName());
        validateProfileUpdateDTO(user, profileUpdateRequestDTO);
        if (!profileUpdateRequestDTO.getUsername().isEmpty()) {
            user.setUsername(profileUpdateRequestDTO.getUsername());
        }
        if (!profileUpdateRequestDTO.getEmail().isEmpty()) {
            user.setEmail(profileUpdateRequestDTO.getEmail());
        }
        if (!profileUpdateRequestDTO.getAvatar().isEmpty()) {
            String avatar = handleImageUpload(profileUpdateRequestDTO.getAvatar());
            deleteImageFile(user.getAvatar());
            user.setAvatar(avatar);
        }
        if (profileUpdateRequestDTO.getFormattedBirthday() != null) {
            user.setBirthday(profileUpdateRequestDTO.getFormattedBirthday());
        }
        if (!profileUpdateRequestDTO.getBio().isEmpty()) {
            user.setBio(profileUpdateRequestDTO.getBio());
        }
        if (!profileUpdateRequestDTO.getLocation().isEmpty()) {
            user.setLocation(profileUpdateRequestDTO.getLocation());
        }
        if (profileUpdateRequestDTO.getGender() != null) {
            user.setGender(profileUpdateRequestDTO.getGender());
        }
        userRepository.save(user);

        String description = "El usuario '" + user.getUsername() + "' (ID: " + user.getUserId() + ") ha actualizado su perfil";
        ActivityLogRequestDTO activityLogRequestDTO = new ActivityLogRequestDTO(user, user.getUserId(), RelatedEntity.USER, ActivityType.PROFILE_EDITED, description);
        activityLogService.createActivityLog(activityLogRequestDTO);
    }

    private void validateProfileUpdateDTO(User user, ProfileUpdateRequestDTO profileUpdateRequestDTO) {
        if (!profileUpdateRequestDTO.getUsername().isEmpty() &&
                !user.getUsername().equals(profileUpdateRequestDTO.getUsername())) {
            Optional<User> userOpt = userRepository.findByUsername(profileUpdateRequestDTO.getUsername());
            if (userOpt.isPresent() && !userOpt.get().getUserId().equals(user.getUserId())) {
                throw new IllegalArgumentException("Ya existe otro usuario con ese username.");
            }
        }
        if (!profileUpdateRequestDTO.getEmail().isEmpty() &&
                !user.getEmail().equals(profileUpdateRequestDTO.getEmail())) {
            Optional<User> userOpt2 = userRepository.findByEmail(profileUpdateRequestDTO.getEmail());
            if (userOpt2.isPresent() && !userOpt2.get().getUserId().equals(user.getUserId())) {
                throw new IllegalArgumentException("Ya existe otro usuario con ese email.");
            }
        }
        if (!profileUpdateRequestDTO.getAvatar().isEmpty() && !isImageValid(profileUpdateRequestDTO.getAvatar())) {
            throw new IllegalArgumentException("Tipo de archivo no permitido. Solo se permiten: JPEG, PNG.");
        }
        if (profileUpdateRequestDTO.getGender() != null && !EnumSet.allOf(Gender.class).contains(profileUpdateRequestDTO.getGender())) {
            throw new IllegalArgumentException("El género especificado no es válido.");
        }
        if (profileUpdateRequestDTO.getFormattedBirthday() != null) {
            try {
                LocalDate birthday = profileUpdateRequestDTO.getFormattedBirthday();
                LocalDate today = LocalDate.now();

                if (birthday.isAfter(today)) {
                    throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura.");
                }

                long age = ChronoUnit.YEARS.between(birthday, today);
                if (age < 12) {
                    throw new IllegalArgumentException("El usuario debe tener al menos 12 años.");
                }
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("El formato de la fecha de nacimiento es inválido. Use 'yyyy-MM-dd'.");
            }
        }
    }

    private boolean isImageValid(MultipartFile image) {
        String fileName = image.getOriginalFilename();
        String contentType = image.getContentType();

        return fileName != null && !image.isEmpty() &&
                fileName.toLowerCase().matches(".*\\.(jpg|jpeg|png)$") &&
                ALLOWED_CONTENT_TYPES.contains(contentType);
    }

    private String handleImageUpload(MultipartFile image) {
        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
        File outputFile = new File(UPLOAD_DIR, fileName);

        try {
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                boolean dirCreated = uploadDir.mkdirs(); // Crear directorios si no existen
                if (!dirCreated) {
                    throw new RuntimeException("No se pudo crear el directorio de subida: " + UPLOAD_DIR);
                }
            }
            InputStream inputStream = image.getInputStream();
            BufferedImage bufferedImage = ImageIO.read(inputStream);

            int width = 500;
            int height = 500;
            Image resizedImage = bufferedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);

            BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = outputImage.createGraphics();
            graphics.drawImage(resizedImage, 0, 0, null);
            graphics.dispose();

            ImageIO.write(outputImage, "jpg", outputFile);

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar la imagen: " + e.getMessage(), e);
        }
        return "/images/uploads/avatars/" + fileName;
    }

    private void deleteImageFile(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty() && !imagePath.equals("/images/uploads/default_avatar.png")) {
            Path path = Paths.get("src/main/resources/static" + imagePath);
            try {
                if (Files.exists(path)) {
                    Files.delete(path);
                }
            } catch (IOException e) {
                throw new RuntimeException("Error al eliminar la imagen: " + e.getMessage(), e);
            }
        }
    }

}
