package de.yannicklem.shoppinglist.core.list.restapi.service;

import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.list.entity.ShoppingList;
import de.yannicklem.shoppinglist.core.list.entity.ShoppingListOnlyName;
import de.yannicklem.shoppinglist.core.item.persistence.ItemService;
import de.yannicklem.shoppinglist.core.user.persistence.SLUserService;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.restutils.service.MyResourceProcessor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;


@Service
public class ShoppingListResourceProcessor extends MyResourceProcessor<ShoppingList> {

    private final SLUserService slUserService;
    private final ItemService itemService;

    @Autowired
    public ShoppingListResourceProcessor(EntityLinks entityLinks, SLUserService slUserService,
        ItemService itemService) {

        super(entityLinks);
        this.slUserService = slUserService;
        this.itemService = itemService;
    }

    public ShoppingList process(ShoppingList entity, SLUser currentUser, String projectionName) {

        if (projectionName != null && projectionName.equals("name_only")) {
            ShoppingListOnlyName onlyname = new ShoppingListOnlyName(entity);
            onlyname.add(getSelfRel(entity));

            return onlyname;
        }

        return super.process(entity, currentUser);
    }


    private Link getSelfRel(ShoppingList shoppingList) {

        return entityLinks.linkToSingleResource(ShoppingList.class, shoppingList.getEntityId()).withSelfRel();
    }


    @Override
    public ShoppingList initializeNestedEntities(ShoppingList shoppingList) {

        Set<SLUser> owners = shoppingList.getOwners();
        Set<SLUser> persistedOwners = new HashSet<>();

        for (SLUser owner : owners) {
            if (owner.getEntityId() != null) {
                persistedOwners.add(slUserService.findById(owner.getEntityId()));
            }
        }

        shoppingList.setOwners(persistedOwners);

        Set<Item> items = shoppingList.getItems();
        Set<Item> persistedItems = new HashSet<>();

        for (Item item : items) {
            if (item.getEntityId() != null) {
                persistedItems.add(itemService.findById(item.getEntityId()));
            }
        }

        shoppingList.setItems(persistedItems);

        return shoppingList;
    }
}
