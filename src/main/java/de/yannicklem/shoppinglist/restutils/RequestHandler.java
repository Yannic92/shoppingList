package de.yannicklem.shoppinglist.restutils;

import de.yannicklem.shoppinglist.core.user.entity.SLUser;


public interface RequestHandler<Type extends RestEntity> {

    void handleBeforeCreate(Type object, SLUser currentUser);


    void handleBeforeUpdate(Type oldObject, Type newObject, SLUser currentUser);


    void handleBeforeRead(Type object, SLUser currentUser);


    void handleBeforeDelete(Type object, SLUser currentUser);
}
