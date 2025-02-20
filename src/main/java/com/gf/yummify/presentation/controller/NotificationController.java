package com.gf.yummify.presentation.controller;

import com.gf.yummify.business.services.NotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
@RequestMapping("/notifications")
public class NotificationController {
    private NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public String showNotifications(Authentication authentication,
                                    Model model,
                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                    @RequestParam(value = "size", defaultValue = "7") int size) {
        model.addAttribute("notifications", notificationService.getNotificationFromLastMonth(authentication, page, size));
        return "notifications/notifications";
    }

    @PostMapping("/markAsRead")
    public String markAsRead(@RequestParam("notificationId") UUID notificationId, Model model) {
        try {
            notificationService.markAsRead(notificationId);
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "error";
        }
        return "redirect:/notifications";
    }

    @PostMapping("/markAllAsRead")
    public String markAllAsRead(Authentication authentication, Model model) {
        try {
            notificationService.markAllAsRead(authentication);
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "error";
        }
        return "redirect:/notifications";
    }
}
