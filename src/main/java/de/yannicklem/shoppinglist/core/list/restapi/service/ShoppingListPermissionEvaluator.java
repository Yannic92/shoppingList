package de.yannicklem.shoppinglist.core.list.restapi.service;

import de.yannicklem.restutils.entity.owned.service.OwnedRestEntityPermissionEvaluator;
import de.yannicklem.restutils.entity.service.PermissionEvaluator;

import de.yannicklem.shoppinglist.core.list.entity.ShoppingList;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public class ShoppingListPermissionEvaluator implements PermissionEvaluator<ShoppingList> {

    private final OwnedRestEntityPermissionEvaluator ownedRestEntityPermissionEvaluator;

    @Override
    public boolean isAllowedToUpdate(ShoppingList oldObject, ShoppingList newObject, SLUser currentUser) {

        return ownedRestEntityPermissionEvaluator.isAllowedToUpdate(oldObject, newObject, currentUser);
    }


    @Override
    public boolean isAllowedToCreate(ShoppingList object, SLUser currentUser) {

        return ownedRestEntityPermissionEvaluator.isAllowedToCreate(object, currentUser);
    }


    @Override
    public boolean isAllowedToDelete(ShoppingList object, SLUser currentUser) {

        return ownedRestEntityPermissionEvaluator.isAllowedToDelete(object, currentUser);
    }


    @Override
    public boolean isAllowedToRead(ShoppingList object, SLUser currentUser) {

        return ownedRestEntityPermissionEvaluator.isAllowedToRead(object, currentUser);
    }
}
