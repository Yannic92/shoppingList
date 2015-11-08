package de.yannicklem.shoppinglist.core.user.security.service;

import de.yannicklem.shoppinglist.core.user.entity.SLUser;

import de.yannicklem.shoppinglist.core.user.security.service.CurrentUserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public class SLUserPermissionEvaluator {

    @NonNull
    private final CurrentUserService currentUserService;

    public boolean currentUserIsAllowedToReadUser(SLUser userToRead) {

        if (currentUserService.currentUserIsAdminOrSystemUser()) {
            return true;
        }

        return userToRead != null && userToRead.equals(currentUserService.getCurrentUser());
    }


    public boolean currentUserIsAllowedToUpdateUser(SLUser userToUpdate) {

        if (currentUserService.currentUserIsAdminOrSystemUser()) {
            return true;
        }

        if (userToUpdate == null) {
            return false;
        }

        return !userToUpdate.isAdmin() && userToUpdate.equals(currentUserService.getCurrentUser());
    }


    public boolean currentUserIsAllowedToCreateUser(SLUser userToCreate) {

        if (currentUserService.currentUserIsAdminOrSystemUser()) {
            return true;
        }

        return userToCreate != null && !userToCreate.isAdmin();
    }


    public boolean currentUserIsAllowedToDeleteUser(SLUser userToDelete) {

        if (currentUserService.currentUserIsAdminOrSystemUser()) {
            return true;
        }

        return userToDelete != null && userToDelete.equals(currentUserService.getCurrentUser());
    }
}
