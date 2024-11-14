package com.gf.yummify.presentation.controller;

import com.gf.yummify.business.services.ShoppingListItemService;
import com.gf.yummify.business.services.ShoppingListService;
import com.gf.yummify.data.entity.ShoppingList;
import com.gf.yummify.data.enums.ListStatus;
import com.gf.yummify.presentation.dto.ShoppingListRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/shoppingLists")
public class ShoppingListController {
    private ShoppingListService shoppingListService;
    private ShoppingListItemService shoppingListItemService;

    public ShoppingListController(ShoppingListService shoppingListService, ShoppingListItemService shoppingListItemService) {
        this.shoppingListService = shoppingListService;
        this.shoppingListItemService = shoppingListItemService;
    }

    @GetMapping()
    public String showLists(@RequestParam(value = "status", required = false) String status,
                            @RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "size", defaultValue = "5") int size,
                            Authentication authentication, Model model) {
        try {
            List<String> listStatuses = Arrays.stream(ListStatus.values())
                    .map(Enum::name)
                    .collect(Collectors.toList());
            model.addAttribute("statuses", listStatuses);

            Page<ShoppingList> shoppingListsPage;

            if (status == null || status.isEmpty()) {
                shoppingListsPage = shoppingListService.findLists(authentication, page, size);
            } else {
                shoppingListsPage = shoppingListService.findByStatus(authentication, ListStatus.valueOf(status), page, size);
            }
            model.addAttribute("shoppingListsPage", shoppingListsPage);
            model.addAttribute("status", status);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", "Estado de lista no válido.");
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

    @PostMapping("/updateItemStatus")
    public @ResponseBody Map<String, Object> updateItemStatus(@RequestParam("itemId") UUID itemId,
                                                              @RequestParam("isPurchased") Boolean isPurchased) {
        Map<String, Object> response = new HashMap<>();
        try {
            shoppingListItemService.updateIsPurchased(itemId, isPurchased);

            response.put("success", true);
            response.put("isPurchased", isPurchased);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return response;
    }
}
