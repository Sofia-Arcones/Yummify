package com.gf.yummify.presentation.controller;

import com.gf.yummify.business.services.ChallengeParticipationService;
import com.gf.yummify.business.services.ChallengeService;
import com.gf.yummify.business.services.RecipeService;
import com.gf.yummify.data.entity.Challenge;
import com.gf.yummify.data.entity.Recipe;
import com.gf.yummify.presentation.dto.ChallengeRequestDTO;
import com.gf.yummify.presentation.dto.ChallengeResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/challenges")
public class ChallengeController {
    private ChallengeService challengeService;
    private RecipeService recipeService;
    private ChallengeParticipationService challengeParticipationService;

    public ChallengeController(ChallengeService challengeService, RecipeService recipeService, ChallengeParticipationService challengeParticipationService) {
        this.challengeService = challengeService;
        this.recipeService = recipeService;
        this.challengeParticipationService = challengeParticipationService;
    }

    @GetMapping("/create")
    public String showChallengeForm() {
        return "challenges/challengeForm";
    }

    @PostMapping
    public String createChallenge(@ModelAttribute @Valid ChallengeRequestDTO challengeRequestDTO,
                                  Model model, Authentication authentication) {
        try {
            System.out.println("Start Date: " + challengeRequestDTO.getStartDate());
            System.out.println("End Date: " + challengeRequestDTO.getEndDate());
            System.out.println(challengeRequestDTO.getTitle());
            ChallengeResponseDTO challengeResponseDTO = challengeService.addChallenge(challengeRequestDTO, authentication);
            model.addAttribute("challenge", challengeResponseDTO);
            return "challenges/challengeSuccess";
        } catch (Exception ex) {
            System.out.println(challengeRequestDTO.getDescription());
            model.addAttribute("challenge", challengeRequestDTO);
            model.addAttribute("error", ex.getMessage());
            return "challenges/challengeForm";
        }
    }

    @GetMapping
    public String showChallenges(@RequestParam(value = "status", required = false) String status,
                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                 @RequestParam(value = "size", defaultValue = "5") int size,
                                 Model model,
                                 Authentication authentication) {
        try {
            boolean isUser = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"));
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

            Page<Challenge> challengePage;
            System.out.println("Status: " + status);
            if (isUser) {
                challengePage = challengeService.findChallengesByIsFinished(page, size, false);
                List<Recipe> userRecipes = recipeService.findRecipesByUser(authentication);
                model.addAttribute("userRecipes", userRecipes);
            } else if (isAdmin) {
                if (status == null || status.isEmpty()) {
                    challengePage = challengeService.findChallenges(page, size);
                } else if ("ending_soon".equals(status)) {
                    challengePage = challengeService.findEndingSoonChallenges(page, size);
                } else {
                    Boolean isFinished = null;
                    if ("finalizado".equals(status)) {
                        isFinished = true;
                    } else if ("en_curso".equals(status)) {
                        isFinished = false;
                    } else {
                        throw new IllegalArgumentException("Estado desconocido: " + status);
                    }
                    challengePage = challengeService.findChallengesByIsFinished(page, size, isFinished);
                }
            } else {
                throw new IllegalStateException("Rol desconocido para el usuario.");
            }
            model.addAttribute("challengesPage", challengePage);
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "error";
        }
        return "challenges/challenges";
    }


    @GetMapping("/{id}/participations")
    public String getChallengeParticipations(@PathVariable UUID id,
                                             @RequestParam(value = "page", defaultValue = "0") int page,
                                             @RequestParam(value = "size", defaultValue = "5") int size,
                                             Model model) {
        try {
            ChallengeResponseDTO challengeResponseDTO = challengeService.findChallengeWithPageParticipations(id, size, page);
            model.addAttribute("challenge", challengeResponseDTO);
        } catch (Exception ex) {

        }
        return "challenges/challengeParticipations";
    }

    @PostMapping("/participate")
    public String participateInChallenge(@RequestParam("recipeId") UUID recipeId,
                                         @RequestParam("challengeId") UUID challengeId,
                                         Model model,
                                         Authentication authentication,
                                         RedirectAttributes redirectAttributes) {
        try {
            challengeService.addParticipationToChallenge(challengeId, recipeId, authentication);
            redirectAttributes.addFlashAttribute("successParticipation", "Has añadido correctamente tu participación");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorParticipation", ex.getMessage());
        }
        return "redirect:/challenges";
    }

    @PostMapping("/select-winners")
    public String selectWinners(@RequestParam List<UUID> selectedWinners,
                                @RequestParam UUID challengeId,
                                RedirectAttributes redirectAttributes,
                                HttpServletRequest request,
                                Authentication authentication) {
        try {
            challengeService.setWinners(selectedWinners, challengeId, authentication);
            redirectAttributes.addFlashAttribute("success", "Ganadores seleccionados con éxito.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/challenges");
    }

}
