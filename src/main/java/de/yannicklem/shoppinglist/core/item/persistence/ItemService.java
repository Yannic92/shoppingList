package de.yannicklem.shoppinglist.core.item.persistence;

import de.yannicklem.restutils.entity.owned.service.OwnedEntityService;

import de.yannicklem.shoppinglist.core.article.Article;
import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;

import java.util.Date;
import java.util.List;


/**
 * @author  Yannic Klem - yann.klem@gmail.com
 */
public interface ItemService extends ItemReadOnlyService, OwnedEntityService<Item, String> {

    @Override
    void delete(Item entity);


    @Override
    List<Item> findItemsOwnedBy(SLUser slUser);


    @Override
    List<Item> findItemsByArticle(Article article);


    @Override
    List<Item> findUnusedItems(Date date);


    @Override
    Long countItemsOfOwner(SLUser user);
}
