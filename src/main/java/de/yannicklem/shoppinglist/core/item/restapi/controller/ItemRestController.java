package de.yannicklem.shoppinglist.core.item.restapi.controller;

import de.yannicklem.restutils.controller.RestEntityController;

import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.item.persistence.ItemService;
import de.yannicklem.shoppinglist.core.item.restapi.service.ItemRequestHandler;
import de.yannicklem.shoppinglist.core.item.restapi.service.ItemResourceProcessor;
import de.yannicklem.shoppinglist.core.list.entity.ShoppingList;
import de.yannicklem.shoppinglist.core.list.restapi.controller.ShoppingListEndpoints;
import de.yannicklem.shoppinglist.core.list.restapi.controller.ShoppingListRestController;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.core.user.persistence.SLUserService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.MediaTypes;

import org.springframework.hateoas.Resources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static org.springframework.web.bind.annotation.RequestMethod.*;


@ExposesResourceFor(Item.class)
@RestController
@RequestMapping( produces = { MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE })
public class ItemRestController extends RestEntityController<Item, String> {

    private final ShoppingListRestController shoppingListRestController;

    @Autowired
    public ItemRestController(SLUserService slUserService, ItemService itemService, ItemRequestHandler requestHandler,
                              ItemResourceProcessor resourceProcessor, EntityLinks entityLinks, ShoppingListRestController shoppingListRestController) {

        super(slUserService, itemService, requestHandler, resourceProcessor, entityLinks);
        this.shoppingListRestController = shoppingListRestController;
    }

    @Override
    @RequestMapping(method = RequestMethod.GET, value = ItemEndpoints.ITEM_ENDPOINT)
    public HttpEntity<? extends Resources<? extends Item>> getAllEntities(Principal principal) {
        return super.getAllEntities(principal);
    }

    @Override
    @RequestMapping(method = RequestMethod.GET, value = ItemEndpoints.ITEM_ENDPOINT + "/{id}")
    public HttpEntity<? extends Item> getSpecificEntity(@PathVariable("id") String s, Principal principal) {
        return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Override
    @RequestMapping(method = RequestMethod.PUT, value = ItemEndpoints.ITEM_ENDPOINT + "/{id}")
    public HttpEntity<? extends Item> putEntity(@RequestBody Item entity, @PathVariable("id") String s, Principal principal) {
        return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = ItemEndpoints.ITEM_ENDPOINT)
    public HttpEntity<? extends Item> postEntity(@RequestBody Item entity, Principal principal) {
        return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Override
    @RequestMapping(method = RequestMethod.DELETE, value = ItemEndpoints.ITEM_ENDPOINT + "/{id}")
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public void deleteEntity(@PathVariable("id") String s, Principal principal) {
        // Not allowed
    }

    @RequestMapping(value = ShoppingListEndpoints.SHOPPING_LISTS_SPECIFIC_ENDPOINT + "/items", method = GET)
    public HttpEntity<Resources<? extends Item>> getItemsOfList(@PathVariable("id") String listId, Principal principal) {

        ShoppingList shoppingList = this.shoppingListRestController.getSpecificEntity(listId, principal).getBody();

        SLUser currentUser = principal == null ? null : slUserService.findById(principal.getName()).orElse(null);

        Set<Item> items = shoppingList.getItems();

        items.forEach(item -> requestHandler.handleRead(item, currentUser));

        List<Item> itemResources = items
                .stream()
                .map(item -> resourceProcessor.process(item, currentUser))
                .collect(toList());

        return new HttpEntity<>(new Resources<>(itemResources));
    }

    @RequestMapping(value = ShoppingListEndpoints.SHOPPING_LISTS_SPECIFIC_ENDPOINT + "/items/{itemId}", method = PUT)
    public HttpEntity<Void> putItem(@RequestBody Item item, @PathVariable("id") String listId,
                        @PathVariable("itemId") String itemId, Principal principal) {

        ShoppingList shoppingList = this.shoppingListRestController.getSpecificEntity(listId, principal).getBody();

        boolean isNewItem = !entityService.exists(itemId);

        super.putEntity(item, itemId, principal);


        if(!shoppingList.getItems().contains(item)) {
            shoppingList.getItems().add(item);
        }

        shoppingList.setLastModified(System.currentTimeMillis());
        shoppingListRestController.putEntity(shoppingList, listId, principal);

        return new ResponseEntity<>(isNewItem ? HttpStatus.CREATED : HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = ShoppingListEndpoints.SHOPPING_LISTS_SPECIFIC_ENDPOINT + "/items", method = POST)
    public void postItem(@RequestBody Item item, @PathVariable("id") String listId, Principal principal) {

        ShoppingList shoppingList = this.shoppingListRestController.getSpecificEntity(listId, principal).getBody();

        super.postEntity(item, principal);

        shoppingList.getItems().add(item);
        shoppingList.setLastModified(System.currentTimeMillis());
        shoppingListRestController.putEntity(shoppingList, listId, principal);
    }

    @RequestMapping(value = ShoppingListEndpoints.SHOPPING_LISTS_SPECIFIC_ENDPOINT + "/items/{itemId}", method = DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable("id") String listId,
                           @PathVariable("itemId") String itemId, Principal principal) {

        ShoppingList shoppingList = this.shoppingListRestController.getSpecificEntity(listId, principal).getBody();

        super.deleteEntity(itemId, principal);

        shoppingList.getItems().removeIf(item -> item.getEntityId().equals(listId));
        shoppingList.setLastModified(System.currentTimeMillis());
        shoppingListRestController.putEntity(shoppingList, listId, principal);
    }
}
