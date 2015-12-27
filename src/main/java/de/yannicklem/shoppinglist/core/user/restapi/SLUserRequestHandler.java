package de.yannicklem.shoppinglist.core.user.restapi;

import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.exception.PermissionDeniedException;
import de.yannicklem.shoppinglist.restutils.RequestHandler;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public class SLUserRequestHandler implements RequestHandler<SLUser> {

    private final SLUserPermissionEvaluator slUserPermissionEvaluator;

    @Override
    public void handleBeforeCreate(SLUser userToCreate, SLUser currentUser) {

        if (!slUserPermissionEvaluator.isAllowedToCreate(userToCreate, currentUser)) {
            throw new PermissionDeniedException("User is not allowed to create a new admin");
        }
    }


    @Override
    public void handleBeforeUpdate(SLUser oldUser, SLUser newUser, SLUser currentUser) {

        if (!slUserPermissionEvaluator.isAllowedToUpdate(oldUser, newUser, currentUser)) {
            throw new PermissionDeniedException(String.format("User is not allowed to update user '%s'",
                    oldUser.getUsername()));
        }
    }


    @Override
    public void handleBeforeRead(SLUser object, SLUser currentUser) {
    }


    @Override
    public void handleBeforeDelete(SLUser slUser, SLUser currentUser) {

        if (!slUserPermissionEvaluator.isAllowedToDelete(slUser, currentUser)) {
            throw new PermissionDeniedException(String.format("User is not allowed to delete user '%s'",
                    slUser == null ? null : slUser.getUsername()));
        }
    }
}
