package de.yannicklem.shoppinglist.core.list.restapi.service;

import de.yannicklem.shoppinglist.core.list.entity.ShoppingList;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.core.user.service.SLUserService;
import de.yannicklem.shoppinglist.restutils.service.MyResourceProcessor;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.hateoas.Resource;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public class ShoppingListResourceProcessor implements MyResourceProcessor<ShoppingList> {

    private final SLUserService slUserService;

    @Override
    public Resource<? extends ShoppingList> toResource(ShoppingList entity, SLUser currentUser) {

        return new Resource<>(entity);
    }


    @Override
    public ShoppingList initializeNestedEntities(ShoppingList shoppingList) {

        Set<SLUser> owners = shoppingList.getOwners();
        Set<SLUser> persistedOwners = new HashSet<>();

        for (SLUser owner : owners) {
            persistedOwners.add(slUserService.findById(owner.getId()));
        }

        shoppingList.setOwners(persistedOwners);

        return shoppingList;
    }
}
