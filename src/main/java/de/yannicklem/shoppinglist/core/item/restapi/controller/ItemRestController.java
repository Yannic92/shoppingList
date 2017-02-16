package de.yannicklem.shoppinglist.core.item.restapi.controller;

import de.yannicklem.restutils.controller.RestEntityController;

import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.item.persistence.ItemService;
import de.yannicklem.shoppinglist.core.item.restapi.service.ItemRequestHandler;
import de.yannicklem.shoppinglist.core.item.restapi.service.ItemResourceProcessor;
import de.yannicklem.shoppinglist.core.user.persistence.SLUserService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.MediaTypes;

import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@ExposesResourceFor(Item.class)
@RestController
@RequestMapping(
    value = ItemEndpoints.ITEM_ENDPOINT, produces = { MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE }
)
public class ItemRestController extends RestEntityController<Item, String> {

    @Autowired
    public ItemRestController(SLUserService slUserService, ItemService itemService, ItemRequestHandler requestHandler,
        ItemResourceProcessor resourceProcessor, EntityLinks entityLinks) {

        super(slUserService, itemService, requestHandler, resourceProcessor, entityLinks);
    }
}
