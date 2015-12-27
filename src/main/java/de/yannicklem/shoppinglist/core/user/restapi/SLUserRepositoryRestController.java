package de.yannicklem.shoppinglist.core.user.restapi;

import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.core.user.registration.entity.Confirmation;
import de.yannicklem.shoppinglist.core.user.service.SLUserService;
import de.yannicklem.shoppinglist.restutils.EntityService;
import de.yannicklem.shoppinglist.restutils.MyRestController;
import de.yannicklem.shoppinglist.restutils.RequestHandler;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.MediaTypes;

import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;


@RequestMapping(
    value = SLUserEndpoints.SLUSER_ENDPOINT, produces = { MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE }
)
@RestController
@ExposesResourceFor(SLUser.class)
public class SLUserRepositoryRestController extends MyRestController<SLUser, String> {

    @Autowired
    public SLUserRepositoryRestController(SLUserService slUserService, EntityService<SLUser, String> entityService,
        RequestHandler<SLUser> requestHandler, EntityLinks entityLinks) {

        super(slUserService, entityService, requestHandler, entityLinks);
    }

    @RequestMapping(method = RequestMethod.GET, value = SLUserEndpoints.SLUSER_CURRENT_ENDPOINT)
    public SLUserResource getCurrentUser(Principal principal) {

        SLUser currentUser = principal == null ? null : slUserService.findById(principal.getName());

        if (currentUser == null) {
            return null;
        }

        SLUserResource slUserResource = new SLUserResource(new SLUserDetailed(currentUser));
        slUserResource.add(entityLinks.linkToSingleResource(SLUser.class, currentUser));

        return slUserResource;
    }


    @RequestMapping(method = RequestMethod.PUT, value = SLUserEndpoints.SLUSER_CONFIRMATION_ENDPOINT)
    public void confirmRegistration(@PathVariable String id, @RequestBody Confirmation confirmation) {

        slUserService.confirmUserRegistration(confirmation, id);
    }
}
