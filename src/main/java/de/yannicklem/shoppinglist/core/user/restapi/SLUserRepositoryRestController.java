package de.yannicklem.shoppinglist.core.user.restapi;

import de.yannicklem.shoppinglist.core.user.SLUser;
import de.yannicklem.shoppinglist.core.user.registration.Confirmation;
import de.yannicklem.shoppinglist.core.user.security.CurrentUserService;
import de.yannicklem.shoppinglist.core.user.service.SLUserService;
import de.yannicklem.shoppinglist.exception.NotFoundException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Resources;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

import java.util.List;
import java.util.stream.Collectors;


@RepositoryRestController
@ExposesResourceFor(SLUser.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public class SLUserRepositoryRestController {

    @NonNull
    private final SLUserService slUserService;

    @NonNull
    private final CurrentUserService currentUserService;

    @RequestMapping(method = RequestMethod.GET, value = SLUserEndpoints.SLUSER_ENDPOINT)
    @ResponseBody
    public Resources<PersistentEntityResource> getSLUsers(PersistentEntityResourceAssembler resourceAssembler) {

        List<SLUser> slUsers = slUserService.findAll();
        List<PersistentEntityResource> resources;
        resources = slUsers.stream().map(resourceAssembler::toResource).collect(Collectors.toList());

        return new Resources<>(resources);
    }


    @RequestMapping(method = RequestMethod.GET, value = SLUserEndpoints.SLUSER_SPECIFIC_ENDPOINT)
    @ResponseBody
    public PersistentEntityResource getSLUsers(@PathVariable String name,
        PersistentEntityResourceAssembler resourceAssembler) {

        SLUser slUser = slUserService.findByName(name);

        if (slUser == null) {
            throw new NotFoundException(String.format("User '%s' not found", name));
        }

        return resourceAssembler.toResource(slUser);
    }


    @RequestMapping(method = RequestMethod.GET, value = SLUserEndpoints.SLUSER_CURRENT_ENDPOINT)
    @ResponseBody
    public PersistentEntityResource getCurrentUser(PersistentEntityResourceAssembler resourceAssembler) {

        return resourceAssembler.toResource(currentUserService.getCurrentUser());
    }


    @RequestMapping(method = RequestMethod.PUT, value = SLUserEndpoints.SLUSER_CONFIRMATION_ENDPOINT)
    @ResponseBody
    public void confirmRegistration(@PathVariable String name, @RequestBody Confirmation confirmation) {

        slUserService.confirmUserRegistration(confirmation, name);
    }
}
