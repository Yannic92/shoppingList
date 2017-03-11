package de.yannicklem.shoppinglist.core.user.restapi.controller;

import de.yannicklem.restutils.controller.RestEntityController;
import de.yannicklem.restutils.entity.service.EntityService;
import de.yannicklem.restutils.service.MyResourceProcessor;
import de.yannicklem.restutils.service.RequestHandler;

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


@RequestMapping(produces = { MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE })
@RestController
@ExposesResourceFor(SLUser.class)
public class SLUserRestController extends RestEntityController<SLUser, String> {

    private final CurrentUserService currentUserService;

    @Autowired
    public SLUserRestController(SLUserService slUserService, EntityService<SLUser, String> entityService,
        RequestHandler<SLUser> requestHandler, MyResourceProcessor<SLUser> resourceProcessor, EntityLinks entityLinks,
        CurrentUserService currentUserService) {

        super(slUserService, entityService, requestHandler, resourceProcessor, entityLinks);
        this.currentUserService = currentUserService;
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
    @RequestMapping(method = RequestMethod.POST, value = SLUserEndpoints.SLUSER_ENDPOINT)
    public HttpEntity<? extends SLUser> postEntity(@RequestBody SLUser entity, Principal principal) {
        return super.postEntity(entity, principal);
    }

    @Override
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = RequestMethod.DELETE, value = SLUserEndpoints.SLUSER_ENDPOINT + "/{id}")
    public void deleteEntity(@PathVariable("id") String s, Principal principal) {
        super.deleteEntity(s, principal);
    }
}
