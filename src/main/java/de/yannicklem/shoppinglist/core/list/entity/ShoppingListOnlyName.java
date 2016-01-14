package de.yannicklem.shoppinglist.core.list.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class ShoppingListOnlyName extends ShoppingList {

    private String name;

    private Long entityId;

    public ShoppingListOnlyName(ShoppingList entity) {

        this.name = entity.getName();
        this.entityId = entity.getEntityId();
    }


    public ShoppingListOnlyName(Long entityId, String name) {

        this.entityId = entityId;
        this.name = name;
    }
}
