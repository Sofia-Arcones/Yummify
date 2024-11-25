package com.gf.yummify.presentation.controller;

import com.gf.yummify.business.services.RelationshipService;
import com.gf.yummify.business.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
                                    RedirectAttributes redirectAttributes,
                                    HttpServletRequest request) {
        try {
            relationshipService.followOrUnfollow(authentication, username);
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/users/profile/" + username);
    }

    @PostMapping("/friend-request")
    public String addOrChangeFriend(Authentication authentication,
                                    @RequestParam("username") String username,
                                    Model model,
                                    RedirectAttributes redirectAttributes,
                                    HttpServletRequest request) {
        try {
            relationshipService.addOrChangeFriend(authentication, username);
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/users/profile/" + username);
    }

    @GetMapping("/relationships")
    public String showRelationships(Authentication authentication,
                                    Model model) {
        try {
            model.addAttribute("receivedFriendRequests", relationshipService.findReceivedFriendRequests(authentication));
            model.addAttribute("friends", relationshipService.findFriends(authentication));
            model.addAttribute("followedList", relationshipService.findFolloweds(authentication));
            model.addAttribute("followersList", relationshipService.findFollowers(authentication));
            System.out.println(relationshipService.findFolloweds(authentication));
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "/relationships/relationships";
    }
}
