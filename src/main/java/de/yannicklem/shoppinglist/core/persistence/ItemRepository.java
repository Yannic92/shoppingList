package de.yannicklem.shoppinglist.core.persistence;

import de.yannicklem.shoppinglist.core.article.entity.Article;
import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ItemRepository extends CrudRepository<Item, Long> {

    @Override
    List<Item> findAll();


    @Query("SELECT i FROM Item i WHERE :user MEMBER OF i.owners")
    List<Item> findItemsOwnedBy(@Param("user") SLUser slUser);


    List<Item> findByArticle(Article article);
}
