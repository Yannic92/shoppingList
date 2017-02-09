package de.yannicklem.restutils.entity.service;

import de.yannicklem.restutils.entity.RestEntity;


/**
 * @author  Yannic Klem - yann.klem@gmail.com
 */
public interface EntityPersistenceHandler<Type extends RestEntity> {


    default void handleBeforeCreate(Type entity) {
    }


    default void handleAfterCreate(Type entity) {
    }


    default void handleBeforeUpdate(Type entity) {
    }


    default void handleAfterUpdate(Type entity) {
    }


    default void handleBeforeDelete(Type entity) {
    }


    default void handleAfterDelete(Type entity) {
    }
}
