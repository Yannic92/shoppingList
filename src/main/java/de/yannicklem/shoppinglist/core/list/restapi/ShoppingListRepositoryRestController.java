package de.yannicklem.shoppinglist.core.list.restapi;

import de.yannicklem.shoppinglist.core.list.entity.ShoppingList;
import de.yannicklem.shoppinglist.core.list.service.ShoppingListService;
import de.yannicklem.shoppinglist.core.user.security.service.CurrentUserService;
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

import java.util.List;
import java.util.stream.Collectors;


@RepositoryRestController
@ExposesResourceFor(ShoppingList.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public class ShoppingListRepositoryRestController {

    @NonNull
    private final ShoppingListService shoppingListService;

    @NonNull
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


//    @RequestMapping(method = RequestMethod.POST, value = ShoppingListEndpoints.SHOPPING_LISTS_ENDPOINT)
//    @ResponseBody
//    @ResponseStatus(HttpStatus.CREATED)
    public PersistentEntityResource postShoppingList(@RequestBody ShoppingList shoppingList,
        PersistentEntityResourceAssembler resourceAssembler) {

        if (shoppingListService.exists(shoppingList)) {
            shoppingListService.handleBeforeSave(shoppingList);
        } else {
            shoppingListService.handleBeforeCreate(shoppingList);
        }

        return resourceAssembler.toResource(shoppingListService.save(shoppingList));
    }
}
