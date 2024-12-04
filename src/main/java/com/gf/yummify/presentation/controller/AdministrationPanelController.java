package com.gf.yummify.presentation.controller;

import com.gf.yummify.business.services.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdministrationPanelController {
    private UserService userService;
    private RecipeService recipeService;
    private CommentService commentService;
    private IngredientService ingredientService;
    private ChallengeService challengeService;

    public AdministrationPanelController(UserService userService, RecipeService recipeService, CommentService commentService, IngredientService ingredientService, ChallengeService challengeService) {
        this.userService = userService;
        this.recipeService = recipeService;
        this.commentService = commentService;
        this.ingredientService = ingredientService;
        this.challengeService = challengeService;
    }

    @GetMapping("/admin/panel")
    public String administratorPanel(Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("userNumber", userService.findAllUsers().size());
            model.addAttribute("recipeNumber", recipeService.findAllRecipes().size());
            model.addAttribute("commentNumber", commentService.findAllComments().size());
            model.addAttribute("ingredientNumber", ingredientService.findAllIngredients().size());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "error";
        }

        return "users/administratorPanel";
    }
}
