package com.gf.yummify.presentation.dto;

import com.gf.yummify.data.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileUpdateRequestDTO {
    private UUID userId;
    private String username;
    private String email;
    private MultipartFile avatar;
    private String bio;
    private Gender gender;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate formattedBirthday;
    private String location;
}
