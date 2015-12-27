package de.yannicklem.shoppinglist.restutils;

import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.core.user.service.SLUserService;

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

import java.security.Principal;

import java.util.ArrayList;
import java.util.List;


public abstract class MyRestController<Type extends RestEntity<ID>, ID> {

    protected final SLUserService slUserService;

    protected final EntityService<Type, ID> entityService;

    protected final RequestHandler<Type> requestHandler;

    protected final EntityLinks entityLinks;

    @Autowired
    public MyRestController(SLUserService slUserService, EntityService<Type, ID> entityService,
        RequestHandler<Type> requestHandler, EntityLinks entityLinks) {

        this.slUserService = slUserService;
        this.entityService = entityService;
        this.requestHandler = requestHandler;
        this.entityLinks = entityLinks;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public HttpEntity<? extends Resource<Type>> getSpecificEntity(@PathVariable ID id) {

        Type specificEntity = entityService.findById(id);

        if (specificEntity == null) {
            return null;
        }

        return new HttpEntity<>(new Resource<>(specificEntity, getSelfRel(specificEntity, id)));
    }


    @RequestMapping(method = RequestMethod.GET)
    public HttpEntity<? extends Resources<? extends Resource<Type>>> getAllEntities() {

        List<Type> all = entityService.findAll();

        List<Resource<Type>> resourcesList = new ArrayList<>();

        for (Type entity : all) {
            resourcesList.add(new Resource<>(entity, getSelfRel(entity, entity.getId())));
        }

        Resources<Resource<Type>> entityResources = new Resources<>(resourcesList);

        if (!all.isEmpty()) {
            entityResources.add(entityLinks.linkToCollectionResource(all.get(0).getClass()));
        }

        return new HttpEntity<>(entityResources);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public HttpEntity<? extends Resource<Type>> putEntity(@RequestBody Type entity, @PathVariable ID id,
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
    public HttpEntity<? extends Resource<Type>> putUser(@RequestBody Type entity, Principal principal) {

        SLUser currentUser = principal == null ? null : slUserService.findById(principal.getName());

        return createEntity(entity, currentUser);
    }


    private HttpEntity<? extends Resource<Type>> createEntity(Type entity, SLUser currentUser) {

        requestHandler.handleBeforeCreate(entity, currentUser);

        Type createdEntity = entityService.create(entity);

        Resource<Type> createdEntityResource = new Resource<>(createdEntity,
                getSelfRel(createdEntity, createdEntity.getId()));

        return new ResponseEntity<>(createdEntityResource, HttpStatus.CREATED);
    }


    private HttpEntity<? extends Resource<Type>> updateEntity(Type entity, SLUser currentUser) {

        requestHandler.handleBeforeUpdate(entityService.findById(entity.getId()), entity, currentUser);

        Type updatedEntity = entityService.update(entity);

        Resource<Type> updatedEntityResource = new Resource<>(updatedEntity,
                getSelfRel(updatedEntity, updatedEntity.getId()));

        return new ResponseEntity<>(updatedEntityResource, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEntity(@PathVariable ID id, Principal principal) {

        SLUser currentUser = principal == null ? null : slUserService.findById(principal.getName());

        Type toDelete = entityService.findById(id);
        requestHandler.handleBeforeDelete(toDelete, currentUser);
        entityService.delete(toDelete);
    }


    private Link getSelfRel(Type entity, ID id) {

        return entityLinks.linkToSingleResource(entity.getClass(), id);
    }
}
