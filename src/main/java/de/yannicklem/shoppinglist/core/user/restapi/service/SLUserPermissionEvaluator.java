package de.yannicklem.shoppinglist.core.user.restapi.service;

import de.yannicklem.restutils.entity.service.PermissionEvaluator;

import de.yannicklem.shoppinglist.core.user.entity.SLUser;

import org.springframework.stereotype.Service;


@Service
public class SLUserPermissionEvaluator implements PermissionEvaluator<SLUser> {

    @Override
    public boolean isAllowedToUpdate(SLUser oldUser, SLUser newUser, SLUser currentUser) {

        if (oldUser == null) {
            return false;
        }

        if (newUser == null) {
            return false;
        }

        if (currentUser == null) {
            return false;
        }

        if (currentUser.isAdmin()) {
            return true;
        }

        if (newUser.isAdmin()) {
            return false;
        }

        return currentUser.getUsername().equals(oldUser.getUsername())
            && currentUser.getUsername().equals(newUser.getUsername());
    }


    @Override
    public boolean isAllowedToCreate(SLUser userToCreate, SLUser currentUser) {

        if (currentUser != null && currentUser.isAdmin()) {
            return true;
        }

        return userToCreate != null && !userToCreate.isAdmin();
    }


    @Override
    public boolean isAllowedToDelete(SLUser userToDelete, SLUser currentUser) {

        if (currentUser == null) {
            return false;
        }

        if (currentUser.isAdmin()) {
            return true;
        }

        return userToDelete != null && userToDelete.equals(currentUser);
    }


    @Override
    public boolean isAllowedToRead(SLUser userToRead, SLUser currentUser) {

        if (currentUser == null) {
            return false;
        }

        if (currentUser.isAdmin()) {
            return true;
        }

        return currentUser.equals(userToRead);
    }
}
