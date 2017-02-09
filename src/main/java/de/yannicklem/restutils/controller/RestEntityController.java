package de.yannicklem.restutils.controller;

import de.yannicklem.restutils.entity.RestEntity;
import de.yannicklem.restutils.entity.service.EntityService;
import de.yannicklem.restutils.service.MyResourceProcessor;
import de.yannicklem.restutils.service.RequestHandler;

import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.core.user.persistence.SLUserService;

import lombok.RequiredArgsConstructor;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Resources;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serializable;

import java.security.Principal;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.apache.log4j.Logger.getLogger;

import static java.lang.invoke.MethodHandles.lookup;


@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public abstract class RestEntityController<Type extends RestEntity<ID>, ID extends Serializable> {

    private static final Logger LOGGER = getLogger(lookup().lookupClass());

    protected final SLUserService slUserService;

    protected final EntityService<Type, ID> entityService;

    protected final RequestHandler<Type> requestHandler;

    protected final MyResourceProcessor<Type> resourceProcessor;

    protected final EntityLinks entityLinks;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public HttpEntity<? extends Type> getSpecificEntity(@PathVariable("id") ID id, Principal principal) {

        Optional<Type> specificEntityOptional = entityService.findById(id);

        Type specificEntity = specificEntityOptional.orElse(null);

        SLUser currentUser = principal == null ? null : slUserService.findById(principal.getName()).orElse(null);

        requestHandler.handleRead(specificEntity, currentUser);

        return new HttpEntity<>(resourceProcessor.process(specificEntity, currentUser));
    }


    @RequestMapping(method = RequestMethod.GET)
    public HttpEntity<? extends Resources<? extends Type>> getAllEntities(Principal principal) {

        SLUser currentUser = principal == null ? null : slUserService.findById(principal.getName()).orElse(null);

        List<Type> all = entityService.findAll();
        List<Type> resourcesList = new ArrayList<>();

        for (Type entity : all) {
            try {
                requestHandler.handleRead(entity, currentUser);
                resourcesList.add(resourceProcessor.process(entity, currentUser));
            } catch (Exception e) {
                LOGGER.debug(String.format("Filtered %s with id '%s'", entity.getClass().getTypeName(),
                        entity.getEntityId().toString()));
            }
        }

        Resources<? extends Type> entityResources = new Resources<>(resourcesList);

        if (!all.isEmpty()) {
            entityResources.add(entityLinks.linkToCollectionResource(all.get(0).getClass()));
        }

        return new HttpEntity<>(entityResources);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public HttpEntity<? extends Type> putEntity(@RequestBody Type entity, @PathVariable ID id, Principal principal) {

        entity.setEntityId(id);

        resourceProcessor.initializeNestedEntities(entity);

        SLUser currentUser = principal == null ? null : slUserService.findById(principal.getName()).orElse(null);

        if (!entityService.exists(id)) {
            return createEntity(entity, currentUser);
        } else {
            return updateEntity(entity, currentUser);
        }
    }


    @RequestMapping(method = RequestMethod.POST)
    public HttpEntity<? extends Type> postEntity(@RequestBody Type entity, Principal principal) {

        resourceProcessor.initializeNestedEntities(entity);

        SLUser currentUser = principal == null ? null : slUserService.findById(principal.getName()).orElse(null);

        return createEntity(entity, currentUser);
    }


    protected HttpEntity<? extends Type> createEntity(Type entity, SLUser currentUser) {

        requestHandler.handleBeforeCreate(entity, currentUser);

        Type createdEntity = entityService.create(entity);

        Type createdEntityResource = resourceProcessor.process(createdEntity, currentUser);

        requestHandler.handleAfterCreate(createdEntity, currentUser);

        return new ResponseEntity<>(createdEntityResource, HttpStatus.CREATED);
    }


    protected HttpEntity<? extends Type> updateEntity(Type entity, SLUser currentUser) {

        Type currentEntity = entityService.findById(entity.getEntityId()).orElse(null);

        requestHandler.handleBeforeUpdate(currentEntity, entity, currentUser);

        Type updatedEntity = entityService.update(entity);

        Type updatedEntityResource = resourceProcessor.process(updatedEntity, currentUser);

        requestHandler.handleAfterUpdate(currentEntity, updatedEntity, currentUser);

        return new ResponseEntity<>(updatedEntityResource, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEntity(@PathVariable ID id, Principal principal) {

        SLUser currentUser = principal == null ? null : slUserService.findById(principal.getName()).orElse(null);

        Type toDelete = entityService.findById(id).orElse(null);

        requestHandler.handleBeforeDelete(toDelete, currentUser);

        entityService.delete(toDelete);

        requestHandler.handleAfterDelete(toDelete, currentUser);
    }
}
