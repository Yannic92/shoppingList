package de.yannicklem.shoppinglist.core.list.restapi.service;

import de.yannicklem.shoppinglist.core.list.entity.ShoppingList;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.exception.PermissionDeniedException;
import de.yannicklem.shoppinglist.restutils.service.RequestHandler;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public class ShoppingListRequestHandler implements RequestHandler<ShoppingList> {

    private final ShoppingListPermissionEvaluator shoppingListPermissionEvaluator;

    @Override
    public void handleBeforeCreate(ShoppingList entity, SLUser currentUser) {

        if (currentUser != null && !currentUser.isAdmin()) {
            entity.getOwners().add(currentUser);
        }

        if (!shoppingListPermissionEvaluator.isAllowedToCreate(entity, currentUser)) {
            throw new PermissionDeniedException("Access denied");
        }
    }


    @Override
    public void handleBeforeUpdate(ShoppingList oldEntity, ShoppingList newEntity, SLUser currentUser) {

        if (!shoppingListPermissionEvaluator.isAllowedToUpdate(oldEntity, newEntity, currentUser)) {
            throw new PermissionDeniedException("Access denied");
        }
    }


    @Override
    public void handleRead(ShoppingList entity, SLUser currentUser) {

        if (!shoppingListPermissionEvaluator.isAllowedToRead(entity, currentUser)) {
            throw new PermissionDeniedException("Access denied");
        }
    }


    @Override
    public void handleBeforeDelete(ShoppingList entity, SLUser currentUser) {

        if (!shoppingListPermissionEvaluator.isAllowedToDelete(entity, currentUser)) {
            throw new PermissionDeniedException("Access denied");
        }
    }


    @Override
    public void handleAfterCreate(ShoppingList entity, SLUser currentUser) {

        // TODO: Send email notification to all owners
    }


    @Override
    public void handleAfterUpdate(ShoppingList oldEntity, ShoppingList newEntity, SLUser currentUser) {

        // TODO: Send email notification about changes to all previous owners (previous to have notification about yourself to be removed)
    }


    @Override
    public void handleAfterDelete(ShoppingList entity, SLUser currentUser) {

        // TODO: Send email notification to all owners
    }
}
