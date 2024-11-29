package com.gf.yummify.presentation.dto;

import com.gf.yummify.data.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileUpdateDTO {
    private UUID userId;
    private String username;
    private String email;
    private String avatar;
    private String bio;
    private Gender gender;
    private String formattedBirthday;
    private String location;
}
