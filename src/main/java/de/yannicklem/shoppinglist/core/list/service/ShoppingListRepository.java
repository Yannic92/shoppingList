package de.yannicklem.shoppinglist.core.list.service;

import de.yannicklem.shoppinglist.core.list.entity.ShoppingList;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;


@RepositoryRestResource
public interface ShoppingListRepository extends CrudRepository<ShoppingList, Long> {

    @Override
    List<ShoppingList> findAll();
}
