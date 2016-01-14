package de.yannicklem.shoppinglist.core.item.restapi.service;

import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.list.restapi.service.ShoppingListRequestHandler;
import de.yannicklem.shoppinglist.core.persistence.ItemService;
import de.yannicklem.shoppinglist.core.persistence.ShoppingListValidationService;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.exception.BadRequestException;
import de.yannicklem.shoppinglist.exception.PermissionDeniedException;
import de.yannicklem.shoppinglist.restutils.service.RequestHandler;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public class ItemRequestHandler implements RequestHandler<Item> {

    private static final int MAX_ITEMS_PER_USER = ShoppingListValidationService.MAX_ITEM_COUNT
        * ShoppingListRequestHandler.MAX_LISTS_PER_USER;

    private final ItemPermissionEvaluator itemPermissionEvaluator;
    private final ItemService itemService;

    @Override
    public void handleBeforeCreate(Item entity, SLUser currentUser) {

        if (entity != null && currentUser != null) {
            entity.getOwners().add(currentUser);
        }

        if (!itemPermissionEvaluator.isAllowedToCreate(entity, currentUser)) {
            throw new PermissionDeniedException();
        }

        assert entity != null;

        for (SLUser owner : entity.getOwners()) {
            Long numberOfItems = itemService.countItemsOfOwner(owner);

            if (numberOfItems > MAX_ITEMS_PER_USER) {
                throw new BadRequestException(String.format("Der Nutzer %s hat das Maximum von %d Posten erreicht",
                        owner.getUsername(), MAX_ITEMS_PER_USER));
            }
        }
    }


    @Override
    public void handleBeforeUpdate(Item oldEntity, Item newEntity, SLUser currentUser) {

        if (!itemPermissionEvaluator.isAllowedToUpdate(oldEntity, newEntity, currentUser)) {
            throw new PermissionDeniedException();
        }

        assert newEntity != null;

        for (SLUser owner : newEntity.getOwners()) {
            Long numberOfItems = itemService.countItemsOfOwner(owner);

            if (numberOfItems > MAX_ITEMS_PER_USER) {
                throw new BadRequestException(String.format("Der Nutzer %s hat das Maximum von %d Posten erreicht",
                        owner.getUsername(), MAX_ITEMS_PER_USER));
            }
        }
    }


    @Override
    public void handleRead(Item entity, SLUser currentUser) {

        if (!itemPermissionEvaluator.isAllowedToRead(entity, currentUser)) {
            throw new PermissionDeniedException();
        }
    }


    @Override
    public void handleBeforeDelete(Item entity, SLUser currentUser) {

        if (!itemPermissionEvaluator.isAllowedToDelete(entity, currentUser)) {
            throw new PermissionDeniedException();
        }
    }


    @Override
    public void handleAfterCreate(Item entity, SLUser currentUser) {
    }


    @Override
    public void handleAfterUpdate(Item oldEntity, Item newEntity, SLUser currentUser) {
    }


    @Override
    public void handleAfterDelete(Item entity, SLUser currentUser) {
    }
}
