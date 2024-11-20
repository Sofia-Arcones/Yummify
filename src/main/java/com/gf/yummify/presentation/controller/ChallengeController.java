package com.gf.yummify.presentation.controller;

import com.gf.yummify.business.services.ChallengeService;
import com.gf.yummify.presentation.dto.ChallengeRequestDTO;
import com.gf.yummify.presentation.dto.ChallengeResponseDTO;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/challenges")
public class ChallengeController {
    private ChallengeService challengeService;

    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @GetMapping("/create")
    public String showChallengeForm() {
        return "challenges/challengeForm";
    }

    @PostMapping
    public String createChallenge(@ModelAttribute @Valid ChallengeRequestDTO challengeRequestDTO,
                                  Model model) {
        try {
            System.out.println("Start Date: " + challengeRequestDTO.getStartDate());
            System.out.println("End Date: " + challengeRequestDTO.getEndDate());
            System.out.println(challengeRequestDTO.getTitle());
            ChallengeResponseDTO challengeResponseDTO = challengeService.addChallenge(challengeRequestDTO);
            model.addAttribute("challenge", challengeResponseDTO);
            return "challenges/challengeSuccess";
        } catch (Exception ex) {
            System.out.println(challengeRequestDTO.getDescription());
            model.addAttribute("challenge", challengeRequestDTO);
            model.addAttribute("error", ex.getMessage());
            return "challenges/challengeForm";
        }
    }

}
