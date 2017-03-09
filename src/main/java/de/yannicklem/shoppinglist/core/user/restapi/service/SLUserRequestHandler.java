package de.yannicklem.shoppinglist.core.user.restapi.service;

import de.yannicklem.restutils.service.RequestHandler;

import de.yannicklem.shoppinglist.core.exception.PermissionDeniedException;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public class SLUserRequestHandler implements RequestHandler<SLUser> {

    private final SLUserPermissionEvaluator slUserPermissionEvaluator;

    @Override
    public void handleBeforeCreate(SLUser entityDto, SLUser currentUser) {

        if (!slUserPermissionEvaluator.isAllowedToCreate(entityDto, currentUser)) {
            throw new PermissionDeniedException();
        }

        if (entityDto != null && entityDto.getEmail() != null) {
            entityDto.setEmail(entityDto.getEmail().toLowerCase());
        }
    }


    @Override
    public void handleBeforeUpdate(SLUser oldEntityDto, SLUser newEntityDto, SLUser currentUser) {

        if (!slUserPermissionEvaluator.isAllowedToUpdate(oldEntityDto, newEntityDto, currentUser)) {
            throw new PermissionDeniedException();
        }

        if (newEntityDto != null && newEntityDto.getEmail() != null) {
            newEntityDto.setEmail(newEntityDto.getEmail().toLowerCase());
        }
    }


    @Override
    public void handleRead(SLUser entityDto, SLUser currentUser) {

        if (entityDto.isAdmin() && !currentUser.isAdmin()) {
            throw new PermissionDeniedException();
        }

        if (!entityDto.isEnabled()) {
            throw new PermissionDeniedException();
        }
    }


    @Override
    public void handleBeforeDelete(SLUser entityDto, SLUser currentUser) {

        if (!slUserPermissionEvaluator.isAllowedToDelete(entityDto, currentUser)) {
            throw new PermissionDeniedException();
        }
    }


    @Override
    public void handleAfterCreate(SLUser entityDto, SLUser currentUser) {
    }


    @Override
    public void handleAfterUpdate(SLUser oldEntityDto, SLUser newEntityDto, SLUser currentUser) {
    }


    @Override
    public void handleAfterDelete(SLUser entityDto, SLUser currentUser) {
    }
}
