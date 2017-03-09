package de.yannicklem.restutils.entity.service;

import de.yannicklem.restutils.entity.dto.RestEntityDto;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;


public interface PermissionEvaluator<DtoType extends RestEntityDto> {

    boolean isAllowedToUpdate(DtoType oldObject, DtoType newObject, SLUser currentUser);


    boolean isAllowedToCreate(DtoType object, SLUser currentUser);


    boolean isAllowedToDelete(DtoType object, SLUser currentUser);


    boolean isAllowedToRead(DtoType object, SLUser currentUser);
}
