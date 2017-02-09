package de.yannicklem.shoppinglist.core.list.persistence;

import de.yannicklem.restutils.entity.owned.service.OwnedEntityService;

import de.yannicklem.shoppinglist.core.list.entity.ShoppingList;


/**
 * @author  Yannic Klem - yann.klem@gmail.com
 */
public interface ShoppingListService extends ShoppingListReadOnlyService, OwnedEntityService<ShoppingList, Long> {

    @Override
    void delete(ShoppingList entity);
}
