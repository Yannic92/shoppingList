package de.yannicklem.shoppinglist.core.item.restapi.service;

import de.yannicklem.restutils.service.RequestHandler;

import de.yannicklem.shoppinglist.core.exception.BadRequestException;
import de.yannicklem.shoppinglist.core.exception.PermissionDeniedException;
import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.item.persistence.ItemService;
import de.yannicklem.shoppinglist.core.list.restapi.service.ShoppingListRequestHandler;
import de.yannicklem.shoppinglist.core.list.validation.ShoppingListValidationService;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;

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
    public void handleBeforeCreate(Item entityDto, SLUser currentUser) {

        if (entityDto != null && currentUser != null) {
            entityDto.getOwners().add(currentUser);
        }

        if (!itemPermissionEvaluator.isAllowedToCreate(entityDto, currentUser)) {
            throw new PermissionDeniedException();
        }

        assert entityDto != null;

        for (SLUser owner : entityDto.getOwners()) {
            Long numberOfItems = itemService.countItemsOfOwner(owner);

            if (numberOfItems > MAX_ITEMS_PER_USER) {
                throw new BadRequestException(String.format("Der Nutzer %s hat das Maximum von %d Posten erreicht",
                        owner.getUsername(), MAX_ITEMS_PER_USER));
            }
        }
    }


    @Override
    public void handleBeforeUpdate(Item oldEntityDto, Item newEntityDto, SLUser currentUser) {

        if (!itemPermissionEvaluator.isAllowedToUpdate(oldEntityDto, newEntityDto, currentUser)) {
            throw new PermissionDeniedException();
        }

        assert newEntityDto != null;

        for (SLUser owner : newEntityDto.getOwners()) {
            Long numberOfItems = itemService.countItemsOfOwner(owner);

            if (numberOfItems > MAX_ITEMS_PER_USER) {
                throw new BadRequestException(String.format("Der Nutzer %s hat das Maximum von %d Posten erreicht",
                        owner.getUsername(), MAX_ITEMS_PER_USER));
            }
        }
    }


    @Override
    public void handleRead(Item entityDto, SLUser currentUser) {

        if (!itemPermissionEvaluator.isAllowedToRead(entityDto, currentUser)) {
            throw new PermissionDeniedException();
        }
    }


    @Override
    public void handleBeforeDelete(Item entityDto, SLUser currentUser) {

        if (!itemPermissionEvaluator.isAllowedToDelete(entityDto, currentUser)) {
            throw new PermissionDeniedException();
        }
    }


    @Override
    public void handleAfterCreate(Item entityDto, SLUser currentUser) {
    }


    @Override
    public void handleAfterUpdate(Item oldEntityDto, Item newEntityDto, SLUser currentUser) {
    }


    @Override
    public void handleAfterDelete(Item entityDto, SLUser currentUser) {
    }
}
