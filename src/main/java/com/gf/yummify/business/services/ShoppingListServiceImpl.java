package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.Ingredient;
import com.gf.yummify.data.entity.ShoppingList;
import com.gf.yummify.data.entity.ShoppingListItem;
import com.gf.yummify.data.entity.User;
import com.gf.yummify.data.enums.ListStatus;
import com.gf.yummify.data.enums.UnitOfMeasure;
import com.gf.yummify.data.repository.ShoppingListItemRepository;
import com.gf.yummify.data.repository.ShoppingListRepository;
import com.gf.yummify.presentation.dto.ShoppingListItemRequestDTO;
import com.gf.yummify.presentation.dto.ShoppingListRequestDTO;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class ShoppingListServiceImpl implements ShoppingListService {
    private ShoppingListRepository shoppingListRepository;
    private UserService userService;
    private ShoppingListItemService shoppingListItemService;
    private ShoppingListItemRepository shoppingListItemRepository;
    private IngredientService ingredientService;

    public ShoppingListServiceImpl(ShoppingListRepository shoppingListRepository, UserService userService, ShoppingListItemService shoppingListItemService, ShoppingListItemRepository shoppingListItemRepository, IngredientService ingredientService) {
        this.shoppingListRepository = shoppingListRepository;
        this.userService = userService;
        this.shoppingListItemService = shoppingListItemService;
        this.shoppingListItemRepository = shoppingListItemRepository;
        this.ingredientService = ingredientService;
    }

    @Override
    public Page<ShoppingList> findLists(Authentication authentication, int page, int size) {
        User user = userService.findUserByUsername(authentication.getName());
        List<ListStatus> statuses = List.of(ListStatus.IN_PROGRESS, ListStatus.EMPTY, ListStatus.COMPLETED);
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());

        return shoppingListRepository.findByUserAndListStatusIn(user, statuses, pageable);
    }

    @Override
    public void saveList(ShoppingListRequestDTO shoppingListRequestDTO, Authentication authentication) {
        User user = userService.findUserByUsername(authentication.getName());
        List<ListStatus> statuses = List.of(ListStatus.IN_PROGRESS, ListStatus.EMPTY);
        Optional<ShoppingList> shoppingListOptional = shoppingListRepository.findByUserAndTitleAndListStatusIn(user, shoppingListRequestDTO.getTitle(), statuses);

        if (shoppingListOptional.isPresent()) {
            throw new IllegalArgumentException("Ya tienes una lista sin acabar con ese nombre.");
        } else {
            ShoppingList shoppingList = new ShoppingList();
            shoppingList.setUser(user);
            shoppingList.setTitle(shoppingListRequestDTO.getTitle());

            List<ShoppingListItem> shoppingListItemList = shoppingListItemService.generateShoppingListItems(shoppingListRequestDTO, shoppingList);

            shoppingList.setListStatus(shoppingListItemList.isEmpty() ? ListStatus.EMPTY : ListStatus.IN_PROGRESS);

            shoppingList.setListItems(shoppingListItemList);
            shoppingListRepository.save(shoppingList);

            shoppingListItemRepository.saveAll(shoppingListItemList);
        }
    }

    public Page<ShoppingList> findByStatus(Authentication authentication, ListStatus status, int page, int size) {
        User user = userService.findUserByUsername(authentication.getName());
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());
        return shoppingListRepository.findByUserAndListStatus(user, status, pageable);
    }

    @Override
    public ShoppingList updateListStatus(UUID id, UUID itemId, Boolean isPurchased, Boolean isArchived) {
        ShoppingList shoppingList = new ShoppingList();
        if (itemId != null && id == null) {
            ShoppingListItem shoppingListItem = shoppingListItemService.updateIsPurchased(itemId, isPurchased);
            shoppingList = shoppingListItem.getShoppingList();
        } else {
            shoppingList = findListById(id);
        }
        if (isArchived) {
            shoppingList.setListStatus(ListStatus.ARCHIVED);
        } else if (shoppingList.getListItems().isEmpty()) {
            shoppingList.setListStatus(ListStatus.EMPTY);
        } else {
            boolean isComplete = true;
            for (ShoppingListItem item : shoppingList.getListItems()) {
                if (!item.getIsPurchased()) {
                    isComplete = false;
                    break;
                }
            }
            if (isComplete) {
                shoppingList.setListStatus(ListStatus.COMPLETED);
            } else {
                shoppingList.setListStatus(ListStatus.IN_PROGRESS);
            }
        }
        return shoppingListRepository.save(shoppingList);
    }

    @Override
    public ShoppingList findListById(UUID id) {
        return shoppingListRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("La lista con id: " + id + " no existe"));
    }

    @Override
    @Transactional
    public String addIngredientToList(ShoppingListItemRequestDTO shoppingListItemRequestDTO) {
        ShoppingList shoppingList = findListById(shoppingListItemRequestDTO.getShoppingListId());
        Ingredient ingredient = ingredientService.findOrCreateIngredient(shoppingListItemRequestDTO.getIngredientName());
        if (ingredient == null) {
            throw new IllegalArgumentException("El ingrediente no pudo ser creado o encontrado.");
        }

        ShoppingListItem shoppingListItem = new ShoppingListItem();
        shoppingListItem.setShoppingList(shoppingList);
        shoppingListItem.setIngredient(ingredient);
        shoppingListItem.setIsPurchased(false);
        shoppingListItem.setQuantity(shoppingListItemRequestDTO.getQuantity());
        shoppingListItem.setUnitOfMeasure(UnitOfMeasure.valueOf(shoppingListItemRequestDTO.getUnitOfMeasure()));
        shoppingListItemRepository.save(shoppingListItem);

        shoppingList.getListItems().add(shoppingListItem);
        shoppingList.setListStatus(ListStatus.IN_PROGRESS);
        shoppingListRepository.save(shoppingList);

        String result = shoppingListItemRequestDTO.getIngredientName() + " añadido correctamente a la lista: " + shoppingList.getTitle();
        System.out.println(result);
        return result;
    }


    @Override
    public List<ShoppingList> findListsByUser(Authentication authentication) {
        User user = userService.findUserByUsername(authentication.getName());
        List<ListStatus> statuses = List.of(ListStatus.IN_PROGRESS, ListStatus.EMPTY, ListStatus.COMPLETED);
        return shoppingListRepository.findByUserAndListStatusIn(user, statuses);
    }
}
