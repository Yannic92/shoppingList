package de.yannicklem.shoppinglist.core.item.entity;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "default", types = Item.class)
public interface ItemProjection {

    Integer getCount();
}
