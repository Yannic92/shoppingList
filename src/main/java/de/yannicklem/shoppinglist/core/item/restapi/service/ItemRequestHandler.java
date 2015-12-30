package de.yannicklem.shoppinglist.core.item.restapi.service;

import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.exception.PermissionDeniedException;
import de.yannicklem.shoppinglist.restutils.service.RequestHandler;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public class ItemRequestHandler implements RequestHandler<Item> {

    private final ItemPermissionEvaluator itemPermissionEvaluator;

    @Override
    public void handleBeforeCreate(Item entity, SLUser currentUser) {

        if (entity != null && currentUser != null) {
            entity.getOwners().add(currentUser);
        }

        if (!itemPermissionEvaluator.isAllowedToCreate(entity, currentUser)) {
            throw new PermissionDeniedException();
        }
    }


    @Override
    public void handleBeforeUpdate(Item oldEntity, Item newEntity, SLUser currentUser) {

        if (!itemPermissionEvaluator.isAllowedToUpdate(oldEntity, newEntity, currentUser)) {
            throw new PermissionDeniedException();
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
