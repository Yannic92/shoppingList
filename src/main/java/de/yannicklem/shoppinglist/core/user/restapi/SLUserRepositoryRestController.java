package de.yannicklem.shoppinglist.core.user.restapi;

import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.core.user.entity.SLUserDetailed;
import de.yannicklem.shoppinglist.core.user.registration.entity.Confirmation;
import de.yannicklem.shoppinglist.core.user.security.service.CurrentUserService;
import de.yannicklem.shoppinglist.core.user.service.SLUserService;
import de.yannicklem.shoppinglist.exception.NotFoundException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import org.springframework.hateoas.*;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
    
    private final EntityLinks entityLinks;
    
    
    @RequestMapping(method = RequestMethod.GET, value = SLUserEndpoints.SLUSER_CURRENT_ENDPOINT)
    @ResponseBody
    public Resource<SLUserDetailed> getCurrentUser(PersistentEntityResourceAssembler resourceAssembler) {

        SLUser currentUser = currentUserService.getCurrentUser();
        currentUser = slUserService.findByName(currentUser.getUsername());
        
        Resource<SLUserDetailed> currentUserResource = new Resource<>(new SLUserDetailed(currentUser));
        Link selfLink = resourceAssembler.toResource(currentUser).getLink("self");
        currentUserResource.add(selfLink);
        
        return currentUserResource;
    }


    @RequestMapping(method = RequestMethod.PUT, value = SLUserEndpoints.SLUSER_CONFIRMATION_ENDPOINT)
    @ResponseBody
    public void confirmRegistration(@PathVariable String name, @RequestBody Confirmation confirmation) {

        slUserService.confirmUserRegistration(confirmation, name);
    }
}
