package com.gf.yummify.presentation.controller;

import com.gf.yummify.business.services.RatingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/rating")
public class RatingController {
    private RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping("/addRating")
    public String addRating(@RequestParam UUID recipeId,
                            @RequestParam Double rating,
                            @RequestParam(required = false) String comment,
                            Authentication authentication,
                            RedirectAttributes redirectAttributes,
                            HttpServletRequest request) {
        try {
            ratingService.addRating(recipeId, rating, comment, authentication);
            redirectAttributes.addFlashAttribute("ratingSuccess", "Tu valoración ha sido añadida.");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            redirectAttributes.addFlashAttribute("ratingError", ex.getMessage());
        }
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/home");
    }

    @PostMapping("/reply")
    public String addCommentToRating(@RequestParam UUID recipeId,
                                     @RequestParam UUID rateId,
                                     @RequestParam String comment, Authentication authentication,
                                     RedirectAttributes redirectAttributes,
                                     HttpServletRequest request) {

        try {
            ratingService.addCommentToRating(recipeId, comment, authentication, rateId);
            redirectAttributes.addFlashAttribute("ratingSuccess", "Tu comentario ha sido añadido.");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            redirectAttributes.addFlashAttribute("ratingError", ex.getMessage());
        }
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/home");

    }

}
