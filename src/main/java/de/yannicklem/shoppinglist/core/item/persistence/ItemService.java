package de.yannicklem.shoppinglist.core.item.persistence;

import de.yannicklem.restutils.entity.owned.service.OwnedEntityService;
import de.yannicklem.shoppinglist.core.article.entity.Article;
import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;

import java.util.Date;
import java.util.List;

/**
 * @author Yannic Klem - yann.klem@gmail.com
 */
public interface ItemService extends ItemReadOnlyService, OwnedEntityService<Item, Long> {

    void delete(Item entity);

    List<Item> findItemsOwnedBy(SLUser slUser);

    List<Item> findItemsByArticle(Article article);

    List<Item> findUnusedItems(Date date);

    Long countItemsOfOwner(SLUser user);
}
