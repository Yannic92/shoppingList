package de.yannicklem.shoppinglist.core.item.persistence;

import de.yannicklem.restutils.entity.service.EntityReadOnlyService;
import de.yannicklem.shoppinglist.core.article.entity.Article;
import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;

import java.util.Date;
import java.util.List;

/**
 * @author Yannic Klem - yann.klem@gmail.com
 */
public interface ItemReadOnlyService extends EntityReadOnlyService<Item, Long> {

    List<Item> findItemsOwnedBy(SLUser slUser);

    List<Item> findItemsByArticle(Article article);

    List<Item> findUnusedItems(Date date);

    Long countItemsOfOwner(SLUser user);
}
