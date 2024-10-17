package com.gf.yummify.presentation.controller;

import com.gf.yummify.business.services.IngredientService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ingredient")
public class IngredientController {

    private IngredientService ingredientService;


}
