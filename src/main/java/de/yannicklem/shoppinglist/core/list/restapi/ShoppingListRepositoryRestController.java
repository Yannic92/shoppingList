package de.yannicklem.shoppinglist.core.list.restapi;

import de.yannicklem.shoppinglist.core.list.entity.ShoppingList;
import de.yannicklem.shoppinglist.core.list.service.ShoppingListService;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.core.user.security.service.CurrentUserService;
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

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RepositoryRestController
@ExposesResourceFor(ShoppingList.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public class ShoppingListRepositoryRestController {

    private final ShoppingListService shoppingListService;
    
    private final SLUserService slUserService;

    private final CurrentUserService currentUserService;

    @RequestMapping(method = RequestMethod.GET, value = ShoppingListEndpoints.SHOPPING_LISTS_ENDPOINT)
    @ResponseBody
    public Resources<PersistentEntityResource> getShoppingList(PersistentEntityResourceAssembler resourceAssembler) {

        List<ShoppingList> shoppingLists = shoppingListService.findAll();
        List<PersistentEntityResource> resources;
        resources = shoppingLists.stream().map(resourceAssembler::toResource).collect(Collectors.toList());

        return new Resources<>(resources);
    }


    @RequestMapping(method = RequestMethod.GET, value = ShoppingListEndpoints.SHOPPING_LISTS_SPECIFIC_ENDPOINT)
    @ResponseBody
    public PersistentEntityResource getShoppingList(@PathVariable Long id,
        PersistentEntityResourceAssembler resourceAssembler) {

        ShoppingList shoppingList = shoppingListService.findById(id);

        if (shoppingList == null) {
            throw new NotFoundException(String.format("ShoppingList with id '%d' not found", id));
        }

        return resourceAssembler.toResource(shoppingList);
    }


    @RequestMapping(method = RequestMethod.POST, value = ShoppingListEndpoints.SHOPPING_LISTS_ENDPOINT)
    @ResponseBody
    public PersistentEntityResource postShoppingList(@RequestBody ShoppingList shoppingList,
        PersistentEntityResourceAssembler resourceAssembler) {

        return putOrPostShoppingList(shoppingList, resourceAssembler);
    }


    @RequestMapping(method = RequestMethod.PUT, value = ShoppingListEndpoints.SHOPPING_LISTS_SPECIFIC_ENDPOINT)
    @ResponseBody
    public PersistentEntityResource putShoppingList(@PathVariable Long id, @RequestBody ShoppingList shoppingList,
                                                     PersistentEntityResourceAssembler resourceAssembler) {
        shoppingList.setId(id);
        return putOrPostShoppingList(shoppingList, resourceAssembler);
    }
    
    private PersistentEntityResource putOrPostShoppingList(ShoppingList shoppingList,
                                                     PersistentEntityResourceAssembler resourceAssembler){
        Set<SLUser> owners = new HashSet<>();

        for(SLUser owner : shoppingList.getOwners()){
            owners.add(slUserService.findByName(owner.getUsername()));
        }
        shoppingList.setOwners(owners);

        if (shoppingListService.exists(shoppingList)) {
            shoppingListService.handleBeforeSave(shoppingList);
        } else {
            shoppingListService.handleBeforeCreate(shoppingList);
        }

        return resourceAssembler.toResource(shoppingListService.save(shoppingList));
    }
}
