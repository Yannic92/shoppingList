package de.yannicklem.shoppinglist.core.user.restapi.controller;

import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.core.user.persistence.SLUserService;
import de.yannicklem.shoppinglist.core.user.registration.entity.Confirmation;
import de.yannicklem.shoppinglist.restutils.controller.MyRestController;
import de.yannicklem.shoppinglist.restutils.service.EntityService;
import de.yannicklem.shoppinglist.restutils.service.MyResourceProcessor;
import de.yannicklem.shoppinglist.restutils.service.RequestHandler;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.MediaTypes;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;


@RequestMapping(
    value = SLUserEndpoints.SLUSER_ENDPOINT, produces = { MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE }
)
@RestController
@ExposesResourceFor(SLUser.class)
public class SLUserRestController extends MyRestController<SLUser, String> {

    @Autowired
    public SLUserRestController(SLUserService slUserService, EntityService<SLUser, String> entityService,
        RequestHandler<SLUser> requestHandler, MyResourceProcessor<SLUser> resourceProcessor, EntityLinks entityLinks) {

        super(slUserService, entityService, requestHandler, resourceProcessor, entityLinks);
    }

    @RequestMapping(method = RequestMethod.GET, value = SLUserEndpoints.SLUSER_CURRENT_ENDPOINT)
    public SLUser getCurrentUser(Principal principal) {

        SLUser currentUser = principal == null ? null : slUserService.findById(principal.getName());

        if (currentUser == null) {
            return null;
        }

        return resourceProcessor.process(currentUser, currentUser);
    }


    @RequestMapping(method = RequestMethod.PUT, value = SLUserEndpoints.SLUSER_CONFIRMATION_ENDPOINT)
    public void confirmRegistration(@PathVariable String id, @RequestBody Confirmation confirmation) {

        slUserService.confirmUserRegistration(confirmation, id);
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
}
