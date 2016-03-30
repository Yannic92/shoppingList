package de.yannicklem.shoppinglist.core.item.persistence;

import de.yannicklem.shoppinglist.core.article.entity.Article;
import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;


public interface ItemRepository extends CrudRepository<Item, Long> {

    @Override
    List<Item> findAll();


    @Query("SELECT i FROM Item i WHERE :user MEMBER OF i.owners")
    List<Item> findItemsOwnedBy(@Param("user") SLUser slUser);


    List<Item> findByArticle(Article article);


    @Query(
        "SELECT i FROM Item i WHERE i.createdAt < :date AND i NOT IN (SELECT DISTINCT iTemp FROM ShoppingList s INNER JOIN s.items iTemp) "
    )
    List<Item> findUnusedItems(@Param("date") Date date);


    @Query("SELECT COUNT(i) FROM Item i WHERE :user MEMBER OF i.owners")
    Long countItemsOfUser(@Param("user") SLUser user);
}
