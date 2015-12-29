package de.yannicklem.shoppinglist.core.item.service;

import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.user.security.service.CurrentUserService;
import de.yannicklem.shoppinglist.restutils.service.EntityService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public class ItemService implements EntityService<Item, Long> {

    private final CurrentUserService currentUserService;
    private final ItemValidationService itemValidationService;
    private final ItemRepository itemRepository;

    public void handleBeforeCreate(Item item) {

        if (item != null) {
            item.getOwners().add(currentUserService.getCurrentUser());
        }

        itemValidationService.validate(item);
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

        return itemRepository.exists(id);
    }


    @Override
    public Item create(Item entity) {

        handleBeforeCreate(entity);

        return itemRepository.save(entity);
    }


    @Override
    public Item update(Item entity) {

        return itemRepository.save(entity);
    }


    @Override
    public void delete(Item entity) {

        itemRepository.delete(entity);
    }
}
