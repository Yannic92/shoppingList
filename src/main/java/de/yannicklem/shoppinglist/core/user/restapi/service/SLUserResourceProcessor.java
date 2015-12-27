package de.yannicklem.shoppinglist.core.user.restapi.service;

import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.core.user.restapi.SLUserDetailed;
import de.yannicklem.shoppinglist.restutils.service.MyResourceProcessor;

import org.springframework.hateoas.Resource;

import org.springframework.stereotype.Service;


@Service
public class SLUserResourceProcessor implements MyResourceProcessor<SLUser> {

    @Override
    public Resource<? extends SLUser> toResource(SLUser entity, SLUser currentUser) {

        if (currentUser == null) {
            return new Resource<>(entity);
        }

        if (currentUser.isAdmin()) {
            return new Resource<>(new SLUserDetailed(entity));
        }

        if (currentUser.equals(entity)) {
            return new Resource<>(new SLUserDetailed(entity));
        }

        return new Resource<>(entity);
    }
}
