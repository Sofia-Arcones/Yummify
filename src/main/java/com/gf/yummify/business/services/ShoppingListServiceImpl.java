package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.ShoppingList;
import com.gf.yummify.data.entity.ShoppingListItem;
import com.gf.yummify.data.entity.User;
import com.gf.yummify.data.enums.ListStatus;
import com.gf.yummify.data.repository.ShoppingListItemRepository;
import com.gf.yummify.data.repository.ShoppingListRepository;
import com.gf.yummify.presentation.dto.ShoppingListRequestDTO;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShoppingListServiceImpl implements ShoppingListService {
    private ShoppingListRepository shoppingListRepository;
    private UserService userService;
    private ShoppingListItemService shoppingListItemService;
    private ShoppingListItemRepository shoppingListItemRepository;


    public ShoppingListServiceImpl(ShoppingListRepository shoppingListRepository, UserService userService, ShoppingListItemService shoppingListItemService, ShoppingListItemRepository shoppingListItemRepository) {
        this.shoppingListRepository = shoppingListRepository;
        this.userService = userService;
        this.shoppingListItemService = shoppingListItemService;
        this.shoppingListItemRepository = shoppingListItemRepository;
    }

    @Override
    public List<ShoppingList> findAllLists(Authentication authentication) {
        User user = userService.findUserByUsername(authentication.getName());
        System.out.println(shoppingListRepository.findByUser(user));
        return shoppingListRepository.findByUser(user);
    }

    @Override
    public void saveList(ShoppingListRequestDTO shoppingListRequestDTO, Authentication authentication) {
        User user = userService.findUserByUsername(authentication.getName());
        List<ListStatus> statuses = List.of(ListStatus.IN_PROGRESS, ListStatus.CREATED);
        Optional<ShoppingList> shoppingListOptional = shoppingListRepository.findByUserAndTitleAndListStatusIn(user, shoppingListRequestDTO.getTitle(), statuses);

        if (shoppingListOptional.isPresent()) {
            throw new IllegalArgumentException("Ya tienes una lista sin acabar con ese nombre.");
        } else {
            ShoppingList shoppingList = new ShoppingList();
            shoppingList.setUser(user);
            shoppingList.setTitle(shoppingListRequestDTO.getTitle());

            List<ShoppingListItem> shoppingListItemList = shoppingListItemService.generateShoppingListItems(shoppingListRequestDTO, shoppingList);

            shoppingList.setListStatus(shoppingListItemList.isEmpty() ? ListStatus.CREATED : ListStatus.IN_PROGRESS);

            shoppingList.setListItems(shoppingListItemList);
            shoppingListRepository.save(shoppingList);

            shoppingListItemRepository.saveAll(shoppingListItemList);
        }
    }

}
