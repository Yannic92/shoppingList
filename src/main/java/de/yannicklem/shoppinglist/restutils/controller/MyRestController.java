package de.yannicklem.shoppinglist.restutils.controller;

import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.core.user.service.SLUserService;
import de.yannicklem.shoppinglist.restutils.entity.RestEntity;
import de.yannicklem.shoppinglist.restutils.service.EntityService;
import de.yannicklem.shoppinglist.restutils.service.MyResourceProcessor;
import de.yannicklem.shoppinglist.restutils.service.RequestHandler;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
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


@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public abstract class MyRestController<Type extends RestEntity<ID>, ID extends Serializable> {

    protected final SLUserService slUserService;

    protected final EntityService<Type, ID> entityService;

    protected final RequestHandler<Type> requestHandler;

    protected final MyResourceProcessor<Type> resourceProcessor;

    protected final EntityLinks entityLinks;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public HttpEntity<? extends Resource<? extends Type>> getSpecificEntity(@PathVariable ID id, Principal principal) {

        Type specificEntity = entityService.findById(id);

        if (specificEntity == null) {
            return null;
        }

        SLUser currentUser = principal == null ? null : slUserService.findById(principal.getName());

        requestHandler.handleRead(specificEntity, currentUser);

        return new HttpEntity<>(getResource(specificEntity, currentUser));
    }


    @RequestMapping(method = RequestMethod.GET)
    public HttpEntity<? extends Resources<? extends Resource<? extends Type>>> getAllEntities(Principal principal) {

        List<Type> all = entityService.findAll();

        List<Resource<? extends Type>> resourcesList = new ArrayList<>();

        SLUser currentUser = principal == null ? null : slUserService.findById(principal.getName());

        for (Type entity : all) {
            requestHandler.handleRead(entity, currentUser);
            resourcesList.add(getResource(entity, currentUser));
        }

        Resources<Resource<? extends Type>> entityResources = new Resources<>(resourcesList);

        if (!all.isEmpty()) {
            entityResources.add(entityLinks.linkToCollectionResource(all.get(0).getClass()));
        }

        return new HttpEntity<>(entityResources);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public HttpEntity<? extends Resource<? extends Type>> putEntity(@RequestBody Type entity, @PathVariable ID id,
        Principal principal) {

        entity.setId(id);

        SLUser currentUser = principal == null ? null : slUserService.findById(principal.getName());

        if (!entityService.exists(id)) {
            return createEntity(entity, currentUser);
        } else {
            return updateEntity(entity, currentUser);
        }
    }


    @RequestMapping(method = RequestMethod.POST)
    public HttpEntity<? extends Resource<? extends Type>> putUser(@RequestBody Type entity, Principal principal) {

        SLUser currentUser = principal == null ? null : slUserService.findById(principal.getName());

        return createEntity(entity, currentUser);
    }


    protected HttpEntity<? extends Resource<? extends Type>> createEntity(Type entity, SLUser currentUser) {

        requestHandler.handleBeforeCreate(entity, currentUser);

        Type createdEntity = entityService.create(entity);

        Resource<? extends Type> createdEntityResource = getResource(createdEntity, currentUser);

        requestHandler.handleAfterCreate(createdEntity, currentUser);

        return new ResponseEntity<>(createdEntityResource, HttpStatus.CREATED);
    }


    protected HttpEntity<? extends Resource<? extends Type>> updateEntity(Type entity, SLUser currentUser) {

        requestHandler.handleBeforeUpdate(entityService.findById(entity.getId()), entity, currentUser);

        Type updatedEntity = entityService.update(entity);

        Resource<? extends Type> updatedEntityResource = getResource(updatedEntity, currentUser);

        requestHandler.handleAfterUpdate(updatedEntity, currentUser);

        return new ResponseEntity<>(updatedEntityResource, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEntity(@PathVariable ID id, Principal principal) {

        SLUser currentUser = principal == null ? null : slUserService.findById(principal.getName());

        Type toDelete = entityService.findById(id);

        requestHandler.handleBeforeDelete(toDelete, currentUser);

        entityService.delete(toDelete);

        requestHandler.handleAfterDelete(toDelete, currentUser);
    }


    protected Resource<? extends Type> getResource(Type entity, SLUser currentUser) {

        Resource<? extends Type> resource = resourceProcessor.toResource(entity, currentUser);

        if (resource != null) {
            resource.add(getSelfRel(entity, entity.getId()));
        }

        return resource;
    }


    private Link getSelfRel(Type entity, ID id) {

        return entityLinks.linkToSingleResource(entity.getClass(), id);
    }
}
