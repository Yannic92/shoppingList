package de.yannicklem.shoppinglist.core.list.validation;

import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.item.validation.ItemValidationService;
import de.yannicklem.shoppinglist.core.list.entity.ShoppingList;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.core.user.validation.SLUserValidationService;
import de.yannicklem.shoppinglist.exception.EntityInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public class ShoppingListValidationService {

    private static final int MAX_NAME_LENGTH = 50;
    public static final int MAX_ITEM_COUNT = 100;

    private final ItemValidationService itemValidationService;
    private final SLUserValidationService slUserValidationService;

    public void validate(ShoppingList shoppingList) throws EntityInvalidException {

        if (shoppingList == null) {
            throw new EntityInvalidException("shoppingList must not be null");
        }

        validateOwners(shoppingList.getOwners());
        validateItems(shoppingList.getItems());
        validateName(shoppingList.getName());
    }


    private void validateOwners(Set<SLUser> owners) {

        if (owners == null || owners.isEmpty()) {
            throw new EntityInvalidException("Es muss mindestens einen Besitzer geben");
        }

        owners.forEach(slUserValidationService::validate);
    }


    private void validateItems(Set<Item> items) {

        if (items == null) {
            throw new EntityInvalidException("Items must not be null");
        }

        if (items.size() >= MAX_ITEM_COUNT) {
            throw new EntityInvalidException(String.format("Eine Liste darf maximal %d Posten enthalten",
                    MAX_ITEM_COUNT));
        }

        items.forEach(itemValidationService::validate);
    }


    private void validateName(String name) {

        if (name == null || name.isEmpty()) {
            throw new EntityInvalidException("Eine Liste darf keinen leeren Namen haben");
        }

        if (name.length() > MAX_NAME_LENGTH) {
            throw new EntityInvalidException(String.format("Der Name einer Liste darf maximal %d Zeichen lang sein",
                    MAX_NAME_LENGTH));
        }
    }
}
