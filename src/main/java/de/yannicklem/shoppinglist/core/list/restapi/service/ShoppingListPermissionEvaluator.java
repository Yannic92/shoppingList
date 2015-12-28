package de.yannicklem.shoppinglist.core.list.restapi.service;

import de.yannicklem.shoppinglist.core.list.entity.ShoppingList;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.restutils.service.PermissionEvaluator;

import org.springframework.stereotype.Service;


@Service
public class ShoppingListPermissionEvaluator implements PermissionEvaluator<ShoppingList> {

    @Override
    public boolean isAllowedToUpdate(ShoppingList oldObject, ShoppingList newObject, SLUser currentUser) {

        if (currentUser == null) {
            return false;
        }

        if (oldObject == null) {
            return false;
        }

        if (newObject == null) {
            return false;
        }

        if (currentUser.isAdmin()) {
            return true;
        }

        return oldObject.getOwners().contains(currentUser);
    }


    @Override
    public boolean isAllowedToCreate(ShoppingList object, SLUser currentUser) {

        if (currentUser == null) {
            return false;
        }

        if (currentUser.isAdmin()) {
            return true;
        }

        return object.getOwners().contains(currentUser);
    }


    @Override
    public boolean isAllowedToDelete(ShoppingList object, SLUser currentUser) {

        if (currentUser == null) {
            return false;
        }

        if (currentUser.isAdmin()) {
            return true;
        }

        return object != null && object.getOwners().contains(currentUser);
    }


    @Override
    public boolean isAllowedToRead(ShoppingList object, SLUser currentUser) {

        if (currentUser == null) {
            return false;
        }

        if (currentUser.isAdmin()) {
            return true;
        }

        return object != null && object.getOwners().contains(currentUser);
    }
}
