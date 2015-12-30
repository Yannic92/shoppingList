package de.yannicklem.shoppinglist.core.persistence;

import de.yannicklem.shoppinglist.core.article.entity.Article;
import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.exception.AlreadyExistsException;
import de.yannicklem.shoppinglist.restutils.service.EntityService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public class ItemService implements EntityService<Item, Long> {

    private final ItemValidationService itemValidationService;
    private final ItemRepository itemRepository;

    public void handleBeforeCreate(Item item) {

        if (item != null && item.getArticle() != null) {
            item.getArticle().getOwners().addAll(item.getOwners());
        }

        itemValidationService.validate(item);

        if (item != null && exists(item.getEntityId())) {
            throw new AlreadyExistsException("Item already exists");
        }
    }


    public void handleBeforeUpdate(Item item) {

        if (item != null && item.getArticle() != null) {
            item.getArticle().getOwners().addAll(item.getOwners());
        }

        itemValidationService.validate(item);

        if (item != null && !exists(item.getEntityId())) {
            throw new AlreadyExistsException("Item not found");
        }
    }


    @Override
    public Item findById(Long id) {

        return itemRepository.findOne(id);
    }


    @Override
    public List<Item> findAll() {

        return itemRepository.findAll();
    }


    @Override
    public boolean exists(Long id) {

        if (id == null) {
            return false;
        }

        return itemRepository.exists(id);
    }


    @Override
    public Item create(Item entity) {

        handleBeforeCreate(entity);

        return itemRepository.save(entity);
    }


    @Override
    public Item update(Item entity) {

        handleBeforeUpdate(entity);

        return itemRepository.save(entity);
    }


    @Override
    public void delete(Item entity) {

        itemRepository.delete(entity);
    }


    @Override
    public void deleteAll() {

        List<Item> all = findAll();

        for (Item item : all) {
            delete(item);
        }
    }


    public List<Item> findItemsOwnedBy(SLUser slUser) {

        return itemRepository.findItemsOwnedBy(slUser);
    }


    public List<Item> findItemsByArticle(Article article) {

        return itemRepository.findByArticle(article);
    }
}
