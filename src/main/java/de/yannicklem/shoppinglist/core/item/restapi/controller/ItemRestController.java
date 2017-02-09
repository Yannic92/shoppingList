package de.yannicklem.shoppinglist.core.item.restapi.controller;

import de.yannicklem.restutils.controller.RestEntityController;
import de.yannicklem.restutils.entity.service.EntityService;
import de.yannicklem.restutils.service.MyResourceProcessor;
import de.yannicklem.restutils.service.RequestHandler;

import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.user.persistence.SLUserService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.MediaTypes;

import org.springframework.http.MediaType;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@ExposesResourceFor(Item.class)
@RestController
@Transactional
@RequestMapping(
    value = ItemEndpoints.ITEM_ENDPOINT, produces = { MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE }
)
public class ItemRestController extends RestEntityController<Item, Long> {

    @Autowired
    public ItemRestController(SLUserService slUserService, EntityService<Item, Long> entityService,
        RequestHandler<Item> requestHandler, MyResourceProcessor<Item> resourceProcessor, EntityLinks entityLinks) {

        super(slUserService, entityService, requestHandler, resourceProcessor, entityLinks);
    }
}
