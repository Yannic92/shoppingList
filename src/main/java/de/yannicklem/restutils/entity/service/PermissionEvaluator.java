package de.yannicklem.restutils.entity.service;

import de.yannicklem.shoppinglist.core.user.entity.SLUser;


public interface PermissionEvaluator<Type> {

    boolean isAllowedToUpdate(Type oldObject, Type newObject, SLUser currentUser);


    boolean isAllowedToCreate(Type object, SLUser currentUser);


    boolean isAllowedToDelete(Type object, SLUser currentUser);


    boolean isAllowedToRead(Type object, SLUser currentUser);
}
