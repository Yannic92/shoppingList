package de.yannicklem.shoppinglist.core.item.persistence;

import de.yannicklem.restutils.entity.owned.service.AbstractOwnedEntityService;
import de.yannicklem.shoppinglist.core.article.entity.Article;
import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.core.user.security.service.CurrentUserService;
import de.yannicklem.shoppinglist.core.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


@Service
@Transactional
public class ItemService extends AbstractOwnedEntityService<Item, Long> implements ItemReadOnlyService {

    private final ItemRepository itemRepository;
    private final CurrentUserService currentUserService;
    private final ItemPersistenceHandler itemPersistenceHandler;
    private final ItemReadOnlyService itemReadOnlyService;

    @Autowired
    public ItemService(ItemRepository itemRepository, CurrentUserService currentUserService,
                       ItemPersistenceHandler itemPersistenceHandler, ItemReadOnlyService itemReadOnlyService) {

        super(itemRepository, itemPersistenceHandler, currentUserService);
        this.itemRepository = itemRepository;
        this.currentUserService = currentUserService;
        this.itemPersistenceHandler = itemPersistenceHandler;
        this.itemReadOnlyService = itemReadOnlyService;
    }

    @Override
    public void delete(Item entity) {

        if (entity == null) {
            throw new NotFoundException("Item not found");
        }

        SLUser currentUser = currentUserService.getCurrentUser();
        entity.getOwners().remove(currentUser);

        itemPersistenceHandler.handleBeforeDelete(entity);

        itemRepository.delete(entity);

        itemPersistenceHandler.handleAfterDelete(entity);
    }

    @Override
    public List<Item> findItemsOwnedBy(SLUser slUser) {

        return itemReadOnlyService.findItemsOwnedBy(slUser);
    }

    @Override
    public List<Item> findItemsByArticle(Article article) {

        return itemReadOnlyService.findItemsByArticle(article);
    }

    @Override
    public List<Item> findUnusedItems(Date date) {

        return itemReadOnlyService.findUnusedItems(date);
    }

    @Override
    public Long countItemsOfOwner(SLUser user) {

        return itemReadOnlyService.countItemsOfOwner(user);
    }
}
