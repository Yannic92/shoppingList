package de.yannicklem.shoppinglist.core.user.restapi.controller;

import de.yannicklem.restutils.controller.RestEntityController;
import de.yannicklem.restutils.entity.service.EntityService;
import de.yannicklem.restutils.service.MyResourceProcessor;
import de.yannicklem.restutils.service.RequestHandler;

import de.yannicklem.shoppinglist.core.list.entity.ShoppingList;
import de.yannicklem.shoppinglist.core.list.restapi.controller.ShoppingListEndpoints;
import de.yannicklem.shoppinglist.core.list.restapi.controller.ShoppingListRestController;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.core.user.persistence.SLUserService;
import de.yannicklem.shoppinglist.core.user.registration.entity.Confirmation;
import de.yannicklem.shoppinglist.core.user.security.service.CurrentUserService;

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

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


@RequestMapping(produces = { MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE })
@RestController
@ExposesResourceFor(SLUser.class)
public class SLUserRestController extends RestEntityController<SLUser, String> {

    private final CurrentUserService currentUserService;
    private final ShoppingListRestController shoppingListRestController;

    @Autowired
    public SLUserRestController(SLUserService slUserService, EntityService<SLUser, String> entityService,
                                RequestHandler<SLUser> requestHandler, MyResourceProcessor<SLUser> resourceProcessor, EntityLinks entityLinks,
                                CurrentUserService currentUserService, ShoppingListRestController shoppingListRestController) {

        super(slUserService, entityService, requestHandler, resourceProcessor, entityLinks);
        this.currentUserService = currentUserService;
        this.shoppingListRestController = shoppingListRestController;
    }

    @RequestMapping(method = RequestMethod.GET, value = SLUserEndpoints.SLUSER_CURRENT_ENDPOINT)
    public SLUser getCurrentUser() {

        SLUser currentUser = currentUserService.getCurrentUser();

        if (currentUser == null) {
            return null;
        }

        return resourceProcessor.process(currentUser, currentUser);
    }


    @RequestMapping(method = RequestMethod.PUT, value = SLUserEndpoints.SLUSER_CONFIRMATION_ENDPOINT)
    public void confirmRegistration(@PathVariable(name = "id") String username,
        @RequestBody Confirmation confirmation) {

        slUserService.confirmUserRegistration(confirmation, username);
        slUserService.setCurrentUser(username);
    }


    @Override
    protected HttpEntity<? extends SLUser> createEntity(SLUser entity, SLUser currentUser) {

        HttpEntity<? extends SLUser> resp = super.createEntity(entity, currentUser);

        if (currentUser == null) {
            return new ResponseEntity<>(resourceProcessor.process(resp.getBody(), resp.getBody()), HttpStatus.CREATED);
        } else {
            return resp;
        }
    }

    @Override
    @RequestMapping(method = RequestMethod.GET, value = SLUserEndpoints.SLUSER_ENDPOINT + "/{id}")
    public HttpEntity<? extends SLUser> getSpecificEntity(@PathVariable("id") String s, Principal principal) {
        return super.getSpecificEntity(s, principal);
    }

    @Override
    @RequestMapping(method = RequestMethod.GET, value = SLUserEndpoints.SLUSER_ENDPOINT)
    public HttpEntity<? extends Resources<? extends SLUser>> getAllEntities(Principal principal) {
        return super.getAllEntities(principal);
    }

    @Override
    @RequestMapping(method = RequestMethod.PUT, value = SLUserEndpoints.SLUSER_ENDPOINT + "/{id}")
    public HttpEntity<? extends SLUser> putEntity(@RequestBody SLUser entity, @PathVariable("id") String s, Principal principal) {
        return super.putEntity(entity, s, principal);
    }

    @Override
    @RequestMapping(method = POST, value = SLUserEndpoints.SLUSER_ENDPOINT)
    public HttpEntity<? extends SLUser> postEntity(@RequestBody SLUser entity, Principal principal) {
        return super.postEntity(entity, principal);
    }

    @Override
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = RequestMethod.DELETE, value = SLUserEndpoints.SLUSER_ENDPOINT + "/{id}")
    public void deleteEntity(@PathVariable("id") String s, Principal principal) {
        super.deleteEntity(s, principal);
    }

    @RequestMapping(method = POST, value = ShoppingListEndpoints.SHOPPING_LISTS_SPECIFIC_ENDPOINT + "/owners")
    public void addOwnerToList(@PathVariable("id") String listId, @RequestBody SLUser slUser, Principal principal) {
        ShoppingList shoppingList = shoppingListRestController.getSpecificEntity(listId, principal).getBody();

        SLUser existingUser = getSpecificEntity(slUser.getUsername(), principal).getBody();

        shoppingList.getOwners().add(existingUser);
        shoppingList.setLastModified(System.currentTimeMillis());
        shoppingListRestController.putEntity(shoppingList, listId, principal);
    }

    @RequestMapping(method = DELETE, value = ShoppingListEndpoints.SHOPPING_LISTS_SPECIFIC_ENDPOINT + "/owners/{username}")
    public void removeOwnerOfList(@PathVariable("id") String listId, @PathVariable("username") String username, Principal principal) {
        ShoppingList shoppingList = shoppingListRestController.getSpecificEntity(listId, principal).getBody();

        SLUser existingUser = getSpecificEntity(username, principal).getBody();

        shoppingList.getOwners().remove(existingUser);
        shoppingList.setLastModified(System.currentTimeMillis());
        shoppingListRestController.putEntity(shoppingList, listId, principal);
    }
}
