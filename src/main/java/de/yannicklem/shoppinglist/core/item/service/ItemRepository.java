package de.yannicklem.shoppinglist.core.item.service;

import de.yannicklem.shoppinglist.core.item.entity.Item;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ItemRepository extends CrudRepository<Item, Integer>{
}
