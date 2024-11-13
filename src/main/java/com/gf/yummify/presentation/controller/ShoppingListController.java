package com.gf.yummify.presentation.controller;

import com.gf.yummify.business.services.ShoppingListService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/shoppingLists")
public class ShoppingListController {
    private ShoppingListService shoppingListService;

    public ShoppingListController(ShoppingListService shoppingListService) {
        this.shoppingListService = shoppingListService;
    }

    @GetMapping()
    public String showLists(Authentication authentication, Model model) {
        try {
            shoppingListService.findAllLists(authentication);
            model.addAttribute("shoppingLists", shoppingListService.findAllLists(authentication));
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "shoppingLists/shoppingLists";
    }

    @PostMapping()
    public String createList(@RequestParam String title, Authentication authentication, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        try {
            shoppingListService.saveList(title, authentication);

            redirectAttributes.addFlashAttribute("success", "Lista de compra creada con éxito.");

            String referer = request.getHeader("Referer");
            return "redirect:" + (referer != null ? referer : "/shoppingLists");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            redirectAttributes.addFlashAttribute("error", ex.getMessage());

            String referer = request.getHeader("Referer");
            return "redirect:" + (referer != null ? referer : "/shoppingLists");
        }
    }


}
