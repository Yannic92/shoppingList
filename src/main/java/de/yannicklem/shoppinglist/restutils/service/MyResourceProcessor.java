package de.yannicklem.shoppinglist.restutils.service;

import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.restutils.entity.RestEntity;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.hateoas.EntityLinks;


@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public abstract class MyResourceProcessor<Type extends RestEntity> {

    protected final EntityLinks entityLinks;

    public Type process(Type entity, SLUser currentUser) {

        entity.add(entityLinks.linkToSingleResource(entity.getClass(), entity.getEntityId()).withSelfRel());

        return entity;
    }


    public abstract Type initializeNestedEntities(Type entity);
}
