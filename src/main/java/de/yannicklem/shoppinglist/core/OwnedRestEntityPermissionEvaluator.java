package de.yannicklem.shoppinglist.core;

import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.restutils.service.PermissionEvaluator;

import org.springframework.stereotype.Service;


@Service
public class OwnedRestEntityPermissionEvaluator implements PermissionEvaluator<OwnedRestEntity> {

    @Override
    public boolean isAllowedToUpdate(OwnedRestEntity oldObject, OwnedRestEntity newObject, SLUser currentUser) {

        if (currentUser == null) {
            return false;
        }

        if (oldObject == null) {
            return false;
        }

        if (newObject == null) {
            return false;
        }

        if (currentUser.isAdmin()) {
            return true;
        }

        return oldObject.getOwners().contains(currentUser);
    }


    @Override
    public boolean isAllowedToCreate(OwnedRestEntity object, SLUser currentUser) {

        if (currentUser == null) {
            return false;
        }

        if (currentUser.isAdmin()) {
            return true;
        }

        return object.getOwners().contains(currentUser);
    }


    @Override
    public boolean isAllowedToDelete(OwnedRestEntity object, SLUser currentUser) {

        if (currentUser == null) {
            return false;
        }

        if (currentUser.isAdmin()) {
            return true;
        }

        return object != null && object.getOwners().contains(currentUser);
    }


    @Override
    public boolean isAllowedToRead(OwnedRestEntity object, SLUser currentUser) {

        if (currentUser == null) {
            return false;
        }

        if (currentUser.isAdmin()) {
            return true;
        }

        return object != null && object.getOwners().contains(currentUser);
    }
}
