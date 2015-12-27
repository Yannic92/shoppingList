package de.yannicklem.shoppinglist.restutils.service;

import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.restutils.RestEntity;


public interface RequestHandler<Type extends RestEntity> {

    void handleBeforeCreate(Type entity, SLUser currentUser);


    void handleBeforeUpdate(Type oldEntity, Type newEntity, SLUser currentUser);


    void handleRead(Type entity, SLUser currentUser);


    void handleBeforeDelete(Type entity, SLUser currentUser);


    void handleAfterCreate(Type entity, SLUser currentUser);


    void handleAfterUpdate(Type entity, SLUser currentUser);


    void handleAfterDelete(Type entity, SLUser currentUser);
}
