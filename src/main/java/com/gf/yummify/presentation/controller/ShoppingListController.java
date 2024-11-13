package com.gf.yummify.presentation.controller;

import com.gf.yummify.business.services.ShoppingListService;
import com.gf.yummify.presentation.dto.ShoppingListRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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
    public String createList(@ModelAttribute @Valid ShoppingListRequestDTO shoppingListRequestDTO, Authentication authentication, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        try {
            shoppingListService.saveList(shoppingListRequestDTO, authentication);

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
