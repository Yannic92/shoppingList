package de.yannicklem.shoppinglist.restutils.service;

import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.restutils.RestEntity;

import org.springframework.hateoas.Resource;


public interface MyResourceProcessor<Type extends RestEntity> {

    Resource<? extends Type> toResource(Type entity, SLUser currentUser);
}
