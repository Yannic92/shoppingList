package de.yannicklem.shoppinglist.core.user.restapi.service;

import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.exception.PermissionDeniedException;
import de.yannicklem.shoppinglist.restutils.service.RequestHandler;

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
            throw new PermissionDeniedException();
        }
    }


    @Override
    public void handleBeforeUpdate(SLUser oldUser, SLUser newUser, SLUser currentUser) {

        if (!slUserPermissionEvaluator.isAllowedToUpdate(oldUser, newUser, currentUser)) {
            throw new PermissionDeniedException();
        }
    }


    @Override
    public void handleRead(SLUser object, SLUser currentUser) {

        if (object.isAdmin() && !currentUser.isAdmin()) {
            throw new PermissionDeniedException();
        }
    }


    @Override
    public void handleBeforeDelete(SLUser slUser, SLUser currentUser) {

        if (!slUserPermissionEvaluator.isAllowedToDelete(slUser, currentUser)) {
            throw new PermissionDeniedException();
        }
    }


    @Override
    public void handleAfterCreate(SLUser entity, SLUser currentUser) {
    }


    @Override
    public void handleAfterUpdate(SLUser oldEntity, SLUser newEntity, SLUser currentUser) {
    }


    @Override
    public void handleAfterDelete(SLUser entity, SLUser currentUser) {
    }
}
