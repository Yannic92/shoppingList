package de.yannicklem.shoppinglist.core.list.service;

import de.yannicklem.shoppinglist.core.list.entity.ShoppingList;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource
public interface ShoppingListRepository extends CrudRepository<ShoppingList, Long> {
}
