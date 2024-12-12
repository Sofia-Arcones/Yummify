package com.gf.yummify.presentation.controller;

import com.gf.yummify.business.services.RelationshipService;
import com.gf.yummify.business.services.UserService;
import com.gf.yummify.data.enums.Gender;
import com.gf.yummify.data.enums.VerificationStatus;
import com.gf.yummify.presentation.dto.ProfileUpdateDTO;
import com.gf.yummify.presentation.dto.ProfileUpdateRequestDTO;
import com.gf.yummify.presentation.dto.RegisterDTO;
import com.gf.yummify.presentation.dto.UserResponseDTO;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {
    private UserService userService;
    private UserDetailsService userDetailsService;
    private RelationshipService relationshipService;

    public UserController(UserService userService, UserDetailsService userDetailsService, RelationshipService relationshipService) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
        this.relationshipService = relationshipService;
    }

    @PostMapping("/register")
    public String registerUser(@Valid RegisterDTO registerDTO,
                               Model model) {

        try {
            userService.createUser(registerDTO);
            model.addAttribute("success", "Usuario registrado correctamente");
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("user", registerDTO);
        }

        return "users/register";
    }


    @GetMapping("/login")
    public String login() {
        return "users/login";
    }

    @GetMapping("/register")
    public String register() {
        return "users/register";
    }

    @Transactional
    @GetMapping("/users/profile/{username}")
    public String viewProfile(@PathVariable String username, Model model, Authentication authentication) {
        try {
            UserResponseDTO profileUser = userService.findProfileUser(username, relationshipService.followersNumber(username), relationshipService.friendsNumber(username));
            model.addAttribute("user", profileUser);
            if (authentication != null && authentication.isAuthenticated()) {
                model.addAttribute("isOwnProfile", userService.checkUserAuthentication(authentication.getName(), profileUser.getUsername()));
                model.addAttribute("isFriend", relationshipService.isFriend(authentication, username));
                model.addAttribute("isFollowed", relationshipService.isFollowed(authentication, username));
                model.addAttribute("isPending", relationshipService.isPending(authentication, username));
                model.addAttribute("isBlocked", relationshipService.isBlocked(authentication, username));
            } else {
                model.addAttribute("isOwnProfile", false);
            }
            return "users/profile";
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "error";
        }
    }

    @GetMapping("/profile/update")
    public String showProfileUpdate(Authentication authentication,
                                    Model model) {
        try {
            ProfileUpdateDTO profileUpdateDTO = userService.getProfileUpdateDTO(authentication);
            model.addAttribute("genders", Gender.values());
            model.addAttribute("user", profileUpdateDTO);
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "error";
        }
        return "users/profileUpdateForm";
    }

    @PostMapping("/profile/update")
    public String profileUpdate(Authentication authentication,
                                Model model,
                                @ModelAttribute @Valid ProfileUpdateRequestDTO profileUpdateRequestDTO,
                                RedirectAttributes redirectAttributes) {
        try {
            userService.updateProfile(authentication, profileUpdateRequestDTO);
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("user", profileUpdateRequestDTO);
            model.addAttribute("genders", Gender.values());
            return "users/profileUpdateForm";
        }
        redirectAttributes.addFlashAttribute("success", "Perfil actualizado correctamente");
        return "redirect:/profile/update";
    }

    @PostMapping("/profile/verified-request")
    public String verificationStatusRequest(RedirectAttributes redirectAttributes, @RequestParam("username") String username, Authentication authentication) {
        try {
            userService.requestVerification(authentication, username);
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "error";
        }
        return "redirect:/users/profile/" + username;
    }

    @GetMapping("/users/management")
    public String usersManagement(Model model,
                                  @RequestParam(value = "status", required = false) String status,
                                  @RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "size", defaultValue = "6") int size) {
        try {
            model.addAttribute("statuses", VerificationStatus.values());
            model.addAttribute("users", userService.findUsersPage(status, page, size));
            model.addAttribute("status", status);
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "users/userManagement";
    }

}
