package de.yannicklem.shoppinglist.restutils.service;

import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.restutils.entity.RestEntity;

import java.io.Serializable;

import java.util.List;


public interface EntityService<Type extends RestEntity, ID extends Serializable> {

    Type findById(ID id);


    List<Type> findAll(SLUser currentUser);


    boolean exists(ID id);


    Type create(Type entity);


    Type update(Type entity);


    void delete(Type entity);


    void deleteAll();
}
