package de.yannicklem.shoppinglist.restutils;

import java.util.List;


public interface EntityService<Type extends RestEntity<ID>, ID> {

    Type findById(ID id);


    List<Type> findAll();


    boolean exists(ID id);


    Type create(Type entity);


    Type update(Type entity);


    void delete(Type toDelete);
}
