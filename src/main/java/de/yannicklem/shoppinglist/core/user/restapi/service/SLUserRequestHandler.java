package de.yannicklem.shoppinglist.core.user.restapi.service;

import de.yannicklem.restutils.service.RequestHandler;

import de.yannicklem.shoppinglist.core.exception.PermissionDeniedException;
import de.yannicklem.shoppinglist.core.list.entity.ShoppingList;
import de.yannicklem.shoppinglist.core.list.persistence.ShoppingListService;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public class SLUserRequestHandler implements RequestHandler<SLUser> {

    private final SLUserPermissionEvaluator slUserPermissionEvaluator;
    private final ShoppingListService shoppingListService;

    @Override
    public void handleBeforeCreate(SLUser userToCreate, SLUser currentUser) {

        if (!slUserPermissionEvaluator.isAllowedToCreate(userToCreate, currentUser)) {
            throw new PermissionDeniedException();
        }

        if (userToCreate != null && userToCreate.getEmail() != null) {
            userToCreate.setEmail(userToCreate.getEmail().toLowerCase());
        }
    }


    @Override
    public void handleBeforeUpdate(SLUser oldUser, SLUser newUser, SLUser currentUser) {

        if (!slUserPermissionEvaluator.isAllowedToUpdate(oldUser, newUser, currentUser)) {
            throw new PermissionDeniedException();
        }

        if (newUser != null && newUser.getEmail() != null) {
            newUser.setEmail(newUser.getEmail().toLowerCase());
        }
    }


    @Override
    public void handleRead(SLUser userToRead, SLUser currentUser) {

        if (userToRead.isAdmin() && !currentUser.isAdmin()) {
            throw new PermissionDeniedException();
        }

        if (!userToRead.isEnabled()) {
            throw new PermissionDeniedException();
        }
    }


    @Override
    public void handleBeforeDelete(SLUser userToDelete, SLUser currentUser) {

        if (!slUserPermissionEvaluator.isAllowedToDelete(userToDelete, currentUser)) {
            throw new PermissionDeniedException();
        }
    }


    @Override
    public void handleAfterCreate(SLUser entity, SLUser currentUser) {
    }


    @Override
    public void handleAfterUpdate(SLUser oldEntity, SLUser newEntity, SLUser currentUser) {
        List<ShoppingList> listsOwnedBy = shoppingListService.findListsOwnedBy(newEntity);

        listsOwnedBy.forEach(list -> {
            list.setLastModified(System.currentTimeMillis());
            shoppingListService.update(list);
        });
    }


    @Override
    public void handleAfterDelete(SLUser entity, SLUser currentUser) {
    }
}
