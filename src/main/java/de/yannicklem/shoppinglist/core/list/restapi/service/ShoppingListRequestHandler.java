package de.yannicklem.shoppinglist.core.list.restapi.service;

import de.yannicklem.restutils.service.RequestHandler;

import de.yannicklem.shoppinglist.core.exception.BadRequestException;
import de.yannicklem.shoppinglist.core.exception.PermissionDeniedException;
import de.yannicklem.shoppinglist.core.list.entity.ShoppingList;
import de.yannicklem.shoppinglist.core.list.persistence.ShoppingListService;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public class ShoppingListRequestHandler implements RequestHandler<ShoppingList> {

    public static final int MAX_LISTS_PER_USER = 100;
    private final ShoppingListPermissionEvaluator shoppingListPermissionEvaluator;
    private final ShoppingListService shoppingListService;

    @Override
    public void handleBeforeCreate(ShoppingList entityDto, SLUser currentUser) {

        if (currentUser != null && !currentUser.isAdmin()) {
            entityDto.getOwners().add(currentUser);
        }

        if (!shoppingListPermissionEvaluator.isAllowedToCreate(entityDto, currentUser)) {
            throw new PermissionDeniedException("Access denied");
        }

        for (SLUser owner : entityDto.getOwners()) {
            Long numberOfListsOwnedByThisUser = shoppingListService.countListsOf(owner);

            if (numberOfListsOwnedByThisUser >= MAX_LISTS_PER_USER) {
                throw new BadRequestException(String.format("Der Nutzer '%s' hat das Maximum von %d Listen erreicht",
                        owner.getUsername(), MAX_LISTS_PER_USER));
            }
        }
    }


    @Override
    public void handleBeforeUpdate(ShoppingList oldEntityDto, ShoppingList newEntityDto, SLUser currentUser) {

        if (!shoppingListPermissionEvaluator.isAllowedToUpdate(oldEntityDto, newEntityDto, currentUser)) {
            throw new PermissionDeniedException("Access denied");
        }

        for (SLUser owner : newEntityDto.getOwners()) {
            Long numberOfListsOwnedByThisUser = shoppingListService.countListsOf(owner);

            if (numberOfListsOwnedByThisUser >= MAX_LISTS_PER_USER) {
                throw new BadRequestException(String.format("Der Nutzer '%s' hat das Maximum von %d Listen erreicht",
                        owner.getUsername(), MAX_LISTS_PER_USER));
            }
        }
    }


    @Override
    public void handleRead(ShoppingList entityDto, SLUser currentUser) {

        if (!shoppingListPermissionEvaluator.isAllowedToRead(entityDto, currentUser)) {
            throw new PermissionDeniedException("Access denied");
        }
    }


    @Override
    public void handleBeforeDelete(ShoppingList entityDto, SLUser currentUser) {

        if (!shoppingListPermissionEvaluator.isAllowedToDelete(entityDto, currentUser)) {
            throw new PermissionDeniedException("Access denied");
        }
    }


    @Override
    public void handleAfterCreate(ShoppingList entityDto, SLUser currentUser) {

        // TODO: Send email notification to all owners
    }


    @Override
    public void handleAfterUpdate(ShoppingList oldEntityDto, ShoppingList newEntityDto, SLUser currentUser) {

        // TODO: Send email notification about changes to all previous owners (previous to have notification about yourself to be removed)
    }


    @Override
    public void handleAfterDelete(ShoppingList entityDto, SLUser currentUser) {

        // TODO: Send email notification to all owners
    }
}
