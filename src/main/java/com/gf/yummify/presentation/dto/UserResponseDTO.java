package com.gf.yummify.presentation.dto;

import com.gf.yummify.data.entity.Recipe;
import com.gf.yummify.data.enums.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    private String username;
    private String avatar;
    private String bio;
    private String location;
    private String email;
    private VerificationStatus verificationStatus;
    private List<Recipe> recipes;
    private int followers;
    private int friends;
}
