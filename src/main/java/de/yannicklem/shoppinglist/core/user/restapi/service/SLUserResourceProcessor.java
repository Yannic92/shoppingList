package de.yannicklem.shoppinglist.core.user.restapi.service;

import de.yannicklem.restutils.service.MyResourceProcessor;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.core.user.persistence.SLUserService;
import de.yannicklem.shoppinglist.core.user.restapi.SLUserDetailed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import java.util.Optional;


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

        Optional<SLUser> persistedUserOptional = slUserService.findById(entity.getEntityId());

        persistedUserOptional.ifPresent(slUser -> initAttributesWithValuesOfPersistedUser(slUser, entity));

        return entity;
    }

    private void initAttributesWithValuesOfPersistedUser(SLUser persistedUser, SLUser toInitialize) {
        toInitialize.setAuthorities(persistedUser.getAuthorities());
        toInitialize.setConfirmation(persistedUser.getConfirmation());
        toInitialize.setAccountNonExpired(persistedUser.isAccountNonExpired());
        toInitialize.setAccountNonLocked(persistedUser.isAccountNonLocked());
        toInitialize.setCredentialsNonExpired(persistedUser.isCredentialsNonExpired());
        toInitialize.setEnabled(persistedUser.isEnabled());
        toInitialize.setCreatedAt(persistedUser.getCreatedAt());
    }
}
