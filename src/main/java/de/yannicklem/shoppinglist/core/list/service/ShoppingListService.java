package de.yannicklem.shoppinglist.core.list.service;

import de.yannicklem.shoppinglist.core.list.entity.ShoppingList;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.core.user.security.service.CurrentUserService;

import de.yannicklem.shoppinglist.exception.PermissionDeniedException;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RepositoryEventHandler(ShoppingList.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public class ShoppingListService {

    private final CurrentUserService currentUserService;
    private final ShoppingListValidationService shoppingListValidationService;
    private final ShoppingListRepository shoppingListRepository;
    private final ShoppingListPermissionEvaluator shoppingListPermissionEvaluator;

    @HandleBeforeCreate(ShoppingList.class)
    public void handleBeforeCreate(ShoppingList shoppingList) {

        if (shoppingList != null) {
            shoppingList.getOwners().add(currentUserService.getCurrentUser());
        }

        shoppingListValidationService.validate(shoppingList);
    }
    
    public List<ShoppingList> findAll() {

        List<ShoppingList> all = shoppingListRepository.findAll();

        if (all != null && !all.isEmpty()) {
            all = filterAfterRead(all);
        }

        return all;
    }


    private List<ShoppingList> filterAfterRead(List<ShoppingList> shoppingLists) {

        List<ShoppingList> filtered = new ArrayList<>();

        for (ShoppingList shoppingList : shoppingLists) {
            if (shoppingListPermissionEvaluator.currentUserIsAllowedToReadShoppingList(shoppingList)) {
                filtered.add(shoppingList);
            }
        }

        return filtered;
    }

    public ShoppingList findById(Long id) {

        ShoppingList shoppingList = shoppingListRepository.findOne(id);
        
        if(shoppingListPermissionEvaluator.currentUserIsAllowedToReadShoppingList(shoppingList)){
            return shoppingList;
        }

        throw new PermissionDeniedException("Access denied");
    }
}
