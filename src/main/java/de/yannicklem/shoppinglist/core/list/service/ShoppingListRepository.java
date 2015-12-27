package de.yannicklem.shoppinglist.core.list.service;

import de.yannicklem.shoppinglist.core.list.entity.ShoppingList;

import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface ShoppingListRepository extends CrudRepository<ShoppingList, Long> {

    @Override
    List<ShoppingList> findAll();
}
