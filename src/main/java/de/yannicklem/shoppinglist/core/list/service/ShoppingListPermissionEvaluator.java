package de.yannicklem.shoppinglist.core.list.service;

import de.yannicklem.shoppinglist.core.list.entity.ShoppingList;
import de.yannicklem.shoppinglist.core.user.security.service.CurrentUserService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public class ShoppingListPermissionEvaluator {

    private final CurrentUserService currentUserService;

    public boolean currentUserIsAllowedToReadShoppingList(ShoppingList shoppingList) {

        if (currentUserService.currentUserIsAdminOrSystemUser()) {
            return true;
        }

        return shoppingList != null && shoppingList.getOwners().contains(currentUserService.getCurrentUser());
    }


    public boolean currentUserIsAllowedToDeleteShoppingList(ShoppingList shoppingList) {

        if (currentUserService.currentUserIsAdminOrSystemUser()) {
            return true;
        }

        return shoppingList != null && shoppingList.getOwners().contains(currentUserService.getCurrentUser());
    }


    public boolean currentUserIsAllowedToUpdateShoppingList(ShoppingList shoppingList) {

        if (currentUserService.currentUserIsAdminOrSystemUser()) {
            return true;
        }

        return shoppingList != null && shoppingList.getOwners().contains(currentUserService.getCurrentUser());
    }
}
