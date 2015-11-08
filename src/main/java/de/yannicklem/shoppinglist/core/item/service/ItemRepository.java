package de.yannicklem.shoppinglist.core.item.service;

import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.item.entity.ItemProjection;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(excerptProjection = ItemProjection.class)
public interface ItemRepository extends CrudRepository<Item, Long>{
}
