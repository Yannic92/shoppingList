package de.yannicklem.shoppinglist.core.list.persistence;

import de.yannicklem.restutils.entity.service.EntityPersistenceHandler;
import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.list.entity.ShoppingList;
import de.yannicklem.shoppinglist.core.list.validation.ShoppingListValidationService;
import de.yannicklem.shoppinglist.core.exception.AlreadyExistsException;
import de.yannicklem.shoppinglist.core.exception.EntityInvalidException;
import de.yannicklem.shoppinglist.core.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author Yannic Klem - yann.klem@gmail.com
 */
@Service
public class ShoppingListPersistenceHandler implements EntityPersistenceHandler<ShoppingList> {

    private final ShoppingListValidationService shoppingListValidationService;
    private final ShoppingListReadOnlyService shoppingListReadOnlyService;

    @Autowired
    public ShoppingListPersistenceHandler(ShoppingListValidationService shoppingListValidationService,
                                          @Qualifier("readOnlyShoppingListService") ShoppingListReadOnlyService shoppingListReadOnlyService) {

        this.shoppingListValidationService = shoppingListValidationService;
        this.shoppingListReadOnlyService = shoppingListReadOnlyService;
    }

    @Override
    public void handleBeforeCreate(ShoppingList shoppingList) {

        if (shoppingList == null) {
            throw new EntityInvalidException("shoppingList must not be null");
        }

        if (shoppingListReadOnlyService.exists(shoppingList.getEntityId())) {
            throw new AlreadyExistsException("Shopping list already exists");
        }

        shoppingListValidationService.validate(shoppingList);

        Set<Item> items = shoppingList.getItems();

        for (Item item : items) {
            item.setOwners(shoppingList.getOwners());
            item.getArticle().setOwners(shoppingList.getOwners());
        }
    }

    @Override
    public void handleBeforeUpdate(ShoppingList shoppingList) {

        if (shoppingList == null || !shoppingListReadOnlyService.exists(shoppingList.getEntityId())) {
            throw new NotFoundException("Shopping list not found");
        }

        shoppingListValidationService.validate(shoppingList);

        Set<Item> items = shoppingList.getItems();

        for (Item item : items) {
            item.setOwners(shoppingList.getOwners());
            item.getArticle().setOwners(shoppingList.getOwners());
        }
    }

    @Override
    public void handleBeforeDelete(ShoppingList shoppingList) {

        if (shoppingList == null) {
            throw new NotFoundException("shopping list not found.");
        }
    }
}
