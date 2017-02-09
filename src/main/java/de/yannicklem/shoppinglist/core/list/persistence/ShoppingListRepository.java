package de.yannicklem.shoppinglist.core.list.persistence;

import de.yannicklem.restutils.entity.owned.service.OwnedRestEntityRepository;

import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.list.entity.ShoppingList;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ShoppingListRepository extends OwnedRestEntityRepository<ShoppingList, Long> {

    @Query(
        "SELECT shoppingList FROM ShoppingList shoppingList INNER JOIN shoppingList.items listItem WHERE :item = listItem"
    )
    List<ShoppingList> findShoppingListsContainingItem(@Param("item") Item item);


    @Query(
        "SELECT COUNT(shoppingList) FROM ShoppingList shoppingList INNER JOIN shoppingList.owners owner WHERE :user = owner"
    )
    Long countListsOfUser(@Param("user") SLUser currentUser);
}
