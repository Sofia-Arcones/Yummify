package com.gf.yummify.presentation.controller;

import com.gf.yummify.business.services.RelationshipService;
import com.gf.yummify.business.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RelationshipController {
    private RelationshipService relationshipService;
    private UserService userService;

    public RelationshipController(RelationshipService relationshipService, UserService userService) {
        this.relationshipService = relationshipService;
        this.userService = userService;
    }

    @PostMapping("/follow")
    public String addOrDeleteFollow(Authentication authentication,
                                    @RequestParam("username") String username,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        try {
            relationshipService.followOrUnfollow(authentication, username);
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/users/profile/" + username;
    }

    @PostMapping("/friend-request")
    public String addOrChangeFriend(Authentication authentication,
                                    @RequestParam("username") String username,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        try {
            relationshipService.addOrChangeFriend(authentication, username);
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/users/profile/" + username;
    }
}
