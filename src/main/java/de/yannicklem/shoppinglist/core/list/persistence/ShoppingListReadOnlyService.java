package de.yannicklem.shoppinglist.core.list.persistence;

import de.yannicklem.restutils.entity.service.EntityReadOnlyService;

import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.list.entity.ShoppingList;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;

import java.util.List;


/**
 * @author  Yannic Klem - yann.klem@gmail.com
 */
public interface ShoppingListReadOnlyService extends EntityReadOnlyService<ShoppingList, Long> {

    List<ShoppingList> findListsOwnedBy(SLUser slUser);


    List<ShoppingList> findShoppingListsContainingItem(Item entity);


    Long countListsOf(SLUser currentUser);
}
