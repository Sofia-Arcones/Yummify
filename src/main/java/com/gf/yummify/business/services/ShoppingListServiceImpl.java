package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.ShoppingList;
import com.gf.yummify.data.entity.User;
import com.gf.yummify.data.enums.ListStatus;
import com.gf.yummify.data.repository.ShoppingListRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShoppingListServiceImpl implements ShoppingListService {
    private ShoppingListRepository shoppingListRepository;
    private UserService userService;

    public ShoppingListServiceImpl(ShoppingListRepository shoppingListRepository, UserService userService) {
        this.shoppingListRepository = shoppingListRepository;
        this.userService = userService;
    }

    public List<ShoppingList> findAllLists(Authentication authentication) {
        User user = userService.findUserByUsername(authentication.getName());
        System.out.println(shoppingListRepository.findByUser(user));
        return shoppingListRepository.findByUser(user);
    }

    public void saveList(String title, Authentication authentication) {
        User user = userService.findUserByUsername(authentication.getName());
        List<ListStatus> statuses = List.of(ListStatus.IN_PROGRESS, ListStatus.CREATED);
        Optional<ShoppingList> shoppingListOptional = shoppingListRepository.findByUserAndTitleAndListStatusIn(user, title, statuses);
        if (shoppingListOptional.isPresent()) {
            throw new IllegalArgumentException("Ya tienes una lista sin acabar con ese nombre.");
        } else {
            ShoppingList shoppingList = new ShoppingList();
            shoppingList.setUser(user);
            shoppingList.setListStatus(ListStatus.CREATED);
            shoppingList.setTitle(title);
            shoppingListRepository.save(shoppingList);
        }
    }
}
