package de.yannicklem.shoppinglist.core.list.restapi.controller;

import de.yannicklem.shoppinglist.core.list.entity.ShoppingList;
import de.yannicklem.shoppinglist.core.persistence.SLUserService;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.restutils.controller.MyRestController;
import de.yannicklem.shoppinglist.restutils.service.EntityService;
import de.yannicklem.shoppinglist.restutils.service.MyResourceProcessor;
import de.yannicklem.shoppinglist.restutils.service.RequestHandler;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.MediaTypes;

import org.springframework.hateoas.Resources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collection;


@RestController
@ExposesResourceFor(ShoppingList.class)
@RequestMapping(
    value = ShoppingListEndpoints.SHOPPING_LISTS_ENDPOINT, produces = {
        MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE
    }
)
public class ShoppingListRestController extends MyRestController<ShoppingList, Long> {

    @Autowired
    public ShoppingListRestController(SLUserService slUserService, EntityService<ShoppingList, Long> entityService,
        RequestHandler<ShoppingList> requestHandler, MyResourceProcessor<ShoppingList> resourceProcessor,
        EntityLinks entityLinks) {

        super(slUserService, entityService, requestHandler, resourceProcessor, entityLinks);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAll(Principal principal){

        HttpEntity<? extends Resources<? extends ShoppingList>> allEntities = this.getAllEntities(principal);
        Collection<? extends ShoppingList> content = allEntities.getBody().getContent();
        SLUser currentUser = principal == null ? null : slUserService.findById(principal.getName());

        for(ShoppingList shoppingList : content){
            requestHandler.handleBeforeDelete(shoppingList, currentUser);
            entityService.delete(shoppingList);
        }
    }
}
