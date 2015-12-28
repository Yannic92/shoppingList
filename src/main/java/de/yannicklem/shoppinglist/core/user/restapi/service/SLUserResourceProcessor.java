package de.yannicklem.shoppinglist.core.user.restapi.service;

import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.core.user.restapi.SLUserDetailed;
import de.yannicklem.shoppinglist.core.user.service.SLUserService;
import de.yannicklem.shoppinglist.restutils.service.MyResourceProcessor;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.hateoas.Resource;

import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public class SLUserResourceProcessor implements MyResourceProcessor<SLUser> {

    private final SLUserService slUserService;

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


    @Override
    public SLUser initializeNestedEntities(SLUser entity) {

        SLUser persistedUser = slUserService.findById(entity.getId());

        if (persistedUser != null) {
            entity.setAuthorities(persistedUser.getAuthorities());
            entity.setConfirmation(persistedUser.getConfirmation());
            entity.setAccountNonExpired(persistedUser.isAccountNonExpired());
            entity.setAccountNonLocked(persistedUser.isAccountNonLocked());
            entity.setCredentialsNonExpired(persistedUser.isCredentialsNonExpired());
            entity.setEnabled(persistedUser.isEnabled());
        }

        return entity;
    }
}
