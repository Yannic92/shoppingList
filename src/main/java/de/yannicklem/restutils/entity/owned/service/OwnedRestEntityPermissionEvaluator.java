package de.yannicklem.restutils.entity.owned.service;

import de.yannicklem.restutils.entity.owned.dto.OwnedRestEntityDto;
import de.yannicklem.restutils.entity.service.PermissionEvaluator;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import org.springframework.stereotype.Service;


@Service
public class OwnedRestEntityPermissionEvaluator implements PermissionEvaluator<OwnedRestEntityDto> {

    @Override
    public boolean isAllowedToUpdate(OwnedRestEntityDto oldObject, OwnedRestEntityDto newObject, SLUser currentUser) {

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
    public boolean isAllowedToCreate(OwnedRestEntityDto object, SLUser currentUser) {

        if (currentUser == null) {
            return false;
        }

        if (currentUser.isAdmin()) {
            return true;
        }

        return object.getOwners().contains(currentUser);
    }


    @Override
    public boolean isAllowedToDelete(OwnedRestEntityDto object, SLUser currentUser) {

        if (currentUser == null) {
            return false;
        }

        if (currentUser.isAdmin()) {
            return true;
        }

        return object != null && object.getOwners().contains(currentUser);
    }


    @Override
    public boolean isAllowedToRead(OwnedRestEntityDto object, SLUser currentUser) {

        if (currentUser == null) {
            return false;
        }

        if (currentUser.isAdmin()) {
            return true;
        }

        return object != null && object.getOwners().contains(currentUser);
    }
}
