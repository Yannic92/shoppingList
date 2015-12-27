package de.yannicklem.shoppinglist.core.list.restapi;

import de.yannicklem.shoppinglist.core.list.entity.ShoppingList;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;


public class ShoppingListResource extends Resource<ShoppingList> {

    public ShoppingListResource(ShoppingList content, Link... links) {

        super(content, links);
    }


    public ShoppingListResource(ShoppingList content, Iterable<Link> links) {

        super(content, links);
    }
}
