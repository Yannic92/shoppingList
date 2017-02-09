package de.yannicklem.shoppinglist.core.item.persistence;

import de.yannicklem.restutils.entity.service.AbstractEntityReadOnlyService;

import de.yannicklem.shoppinglist.core.article.entity.Article;
import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author  Yannic Klem - yann.klem@gmail.com
 */
@Service("readOnlyItemService")
public class ItemReadOnlyServiceImpl extends AbstractEntityReadOnlyService<Item, Long> implements ItemReadOnlyService {

    private final ItemRepository itemRepository;

    @Autowired
    public ItemReadOnlyServiceImpl(ItemRepository itemRepository) {

        super(itemRepository);
        this.itemRepository = itemRepository;
    }

    @Override
    public List<Item> findItemsOwnedBy(SLUser slUser) {

        if (slUser == null) {
            return new ArrayList<>();
        }

        return itemRepository.findEntitiesOwnedBy(slUser);
    }


    @Override
    public List<Item> findItemsByArticle(Article article) {

        if (article == null) {
            return new ArrayList<>();
        }

        return itemRepository.findByArticle(article);
    }


    @Override
    public List<Item> findUnusedItems(Date date) {

        return itemRepository.findUnusedItems(date);
    }


    @Override
    public Long countItemsOfOwner(SLUser user) {

        Long count = itemRepository.countItemsOfUser(user);

        return count == null ? 0 : count;
    }
}
