package de.yannicklem.shoppinglist.core.list.restapi.controller;

import de.yannicklem.shoppinglist.core.list.entity.ShoppingList;
import de.yannicklem.shoppinglist.core.list.restapi.ShoppingListEndpoints;
import de.yannicklem.shoppinglist.core.list.restapi.ShoppingListResource;
import de.yannicklem.shoppinglist.core.list.service.ShoppingListService;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.core.user.security.service.CurrentUserService;
import de.yannicklem.shoppinglist.core.user.service.SLUserService;
import de.yannicklem.shoppinglist.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Resources;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@RestController
@ExposesResourceFor(ShoppingList.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public class ShoppingListRestController {

    private final ShoppingListService shoppingListService;

    private final SLUserService slUserService;

    private final CurrentUserService currentUserService;

    @RequestMapping(method = RequestMethod.GET, value = ShoppingListEndpoints.SHOPPING_LISTS_ENDPOINT)
    @ResponseBody
    public Resources<ShoppingListResource> getShoppingLists() {

        List<ShoppingList> shoppingLists = shoppingListService.findAll();
        List<ShoppingListResource> resources = new ArrayList<>();

        for (ShoppingList shoppingList : shoppingLists) {
            resources.add(new ShoppingListResource(shoppingList));
        }

        return new Resources<>(resources);
    }


    @RequestMapping(method = RequestMethod.GET, value = ShoppingListEndpoints.SHOPPING_LISTS_SPECIFIC_ENDPOINT)
    @ResponseBody
    public ShoppingListResource getShoppingList(@PathVariable Long id) {

        ShoppingList shoppingList = shoppingListService.findById(id);

        if (shoppingList == null) {
            throw new NotFoundException(String.format("ShoppingList with id '%d' not found", id));
        }

        return new ShoppingListResource(shoppingList);
    }


    @RequestMapping(method = RequestMethod.POST, value = ShoppingListEndpoints.SHOPPING_LISTS_ENDPOINT)
    @ResponseBody
    public ShoppingListResource postShoppingList(@RequestBody ShoppingList shoppingList) {

        return putOrPostShoppingList(shoppingList);
    }


    @RequestMapping(method = RequestMethod.PUT, value = ShoppingListEndpoints.SHOPPING_LISTS_SPECIFIC_ENDPOINT)
    @ResponseBody
    public ShoppingListResource putShoppingList(@PathVariable Long id, @RequestBody ShoppingList shoppingList) {

        shoppingList.setId(id);

        return putOrPostShoppingList(shoppingList);
    }


    private ShoppingListResource putOrPostShoppingList(ShoppingList shoppingList) {

        Set<SLUser> owners = new HashSet<>();

        for (SLUser owner : shoppingList.getOwners()) {
            owners.add(slUserService.findById(owner.getUsername()));
        }

        shoppingList.setOwners(owners);

        if (shoppingListService.exists(shoppingList)) {
            shoppingListService.handleBeforeSave(shoppingList);
        } else {
            shoppingListService.handleBeforeCreate(shoppingList);
        }

        return new ShoppingListResource(shoppingListService.save(shoppingList));
    }
}
