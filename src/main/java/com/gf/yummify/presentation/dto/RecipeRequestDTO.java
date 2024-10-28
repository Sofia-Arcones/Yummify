package com.gf.yummify.presentation.dto;

import com.gf.yummify.data.enums.Difficulty;
import com.gf.yummify.data.enums.UnitOfMeasure;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Data
@AllArgsConstructor
public class RecipeRequestDTO {
    private String title;
    private String description;
    private MultipartFile image;
    private String difficulty;
    private Integer prepTime;
    private List<String> ingredients;
    private List<Double> quantities;
    private List<String> units;
    private List<String> instructions;


   }