package de.yannicklem.shoppinglist.core.item.service;

import de.yannicklem.shoppinglist.core.item.entity.Item;

import org.springframework.data.repository.CrudRepository;


public interface ItemRepository extends CrudRepository<Item, Long> {
}
