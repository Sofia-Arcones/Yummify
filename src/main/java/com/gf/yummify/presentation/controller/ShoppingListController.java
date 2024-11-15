package com.gf.yummify.presentation.controller;

import com.gf.yummify.business.services.ShoppingListItemService;
import com.gf.yummify.business.services.ShoppingListService;
import com.gf.yummify.data.entity.ShoppingList;
import com.gf.yummify.data.enums.ListStatus;
import com.gf.yummify.data.enums.UnitOfMeasure;
import com.gf.yummify.presentation.dto.ShoppingListItemRequestDTO;
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
            List<String> units = Arrays.stream(UnitOfMeasure.values()).map(Enum::name).collect(Collectors.toList());
            model.addAttribute("units", units);

            Page<ShoppingList> shoppingListsPage;

            if (status == null || status.isEmpty()) {
                shoppingListsPage = shoppingListService.findLists(authentication, page, size);
            } else {
                shoppingListsPage = shoppingListService.findByStatus(authentication, ListStatus.valueOf(status), page, size);
            }
            model.addAttribute("shoppingListsPage", shoppingListsPage);
            model.addAttribute("status", status);
            if (shoppingListsPage.isEmpty()) {
                model.addAttribute("error", "No se encontraron listas de la compra.");
            }


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
            ShoppingList shoppingList = shoppingListService.updateListStatus(null, itemId, isPurchased, false);
            response.put("success", true);
            response.put("isPurchased", isPurchased);
            response.put("listStatus", shoppingList.getListStatus());
            response.put("shoppingListId", shoppingList.getShoppingListId());
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return response;
    }

    @PostMapping("/archive")
    public String updateListStatus(UUID id, Boolean archived) {
        shoppingListService.updateListStatus(id, null, null, archived);
        return "redirect:/shoppingLists";
    }

    @PostMapping("/addIngredient")
    public String addIngredient(@ModelAttribute @Valid ShoppingListItemRequestDTO shoppingListItemRequestDTO, RedirectAttributes redirectAttributes) {
        try {
            String result = shoppingListService.addIngredientToList(shoppingListItemRequestDTO);
            System.out.println(result);
            redirectAttributes.addFlashAttribute("success", result);
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage()); 
        }
        return "redirect:/shoppingLists";
    }

}
