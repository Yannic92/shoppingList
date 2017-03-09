package de.yannicklem.shoppinglist.core.item.persistence;

import de.yannicklem.restutils.entity.owned.service.OwnedRestEntityRepository;

import de.yannicklem.shoppinglist.core.article.Article;
import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;


public interface ItemRepository extends OwnedRestEntityRepository<Item, String> {

    List<Item> findByArticle(Article article);


    @Query(
        "SELECT i FROM Item i WHERE i.createdAt < :date AND i NOT IN (SELECT DISTINCT iTemp FROM ShoppingList s INNER JOIN s.items iTemp) "
    )
    List<Item> findUnusedItems(@Param("date") Date date);


    @Query("SELECT COUNT(item) FROM Item item INNER JOIN item.owners owner WHERE :user = owner")
    Long countItemsOfUser(@Param("user") SLUser user);
}
