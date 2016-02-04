package de.yannicklem.shoppinglist.core.user.restapi.service;

import de.yannicklem.shoppinglist.core.user.persistence.SLUserService;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.core.user.restapi.SLUserDetailed;
import de.yannicklem.shoppinglist.restutils.service.MyResourceProcessor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;

import org.springframework.stereotype.Service;


@Service
public class SLUserResourceProcessor extends MyResourceProcessor<SLUser> {

    private final SLUserService slUserService;

    @Autowired
    public SLUserResourceProcessor(EntityLinks entityLinks, SLUserService slUserService) {

        super(entityLinks);

        this.slUserService = slUserService;
    }

    @Override
    public SLUser process(SLUser entity, SLUser currentUser) {

        if (currentUser == null) {
            return super.process(entity, currentUser);
        }

        if (currentUser.isAdmin()) {
            SLUserDetailed slUserDetailed = new SLUserDetailed(entity);
            slUserDetailed.add(getSelfRel(slUserDetailed));

            return slUserDetailed;
        }

        if (currentUser.equals(entity)) {
            SLUserDetailed slUserDetailed = new SLUserDetailed(entity);
            slUserDetailed.add(getSelfRel(slUserDetailed));

            return slUserDetailed;
        }

        return super.process(entity, currentUser);
    }


    private Link getSelfRel(SLUser slUser) {

        return entityLinks.linkToSingleResource(SLUser.class, slUser.getEntityId()).withSelfRel();
    }


    @Override
    public SLUser initializeNestedEntities(SLUser entity) {

        SLUser persistedUser = slUserService.findById(entity.getEntityId());

        if (persistedUser != null) {
            entity.setAuthorities(persistedUser.getAuthorities());
            entity.setConfirmation(persistedUser.getConfirmation());
            entity.setAccountNonExpired(persistedUser.isAccountNonExpired());
            entity.setAccountNonLocked(persistedUser.isAccountNonLocked());
            entity.setCredentialsNonExpired(persistedUser.isCredentialsNonExpired());
            entity.setEnabled(persistedUser.isEnabled());
            entity.setCreatedAt(persistedUser.getCreatedAt());
        }

        return entity;
    }
}
