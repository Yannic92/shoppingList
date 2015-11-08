package de.yannicklem.shoppinglist.core.item.service;

import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.list.entity.ShoppingList;
import de.yannicklem.shoppinglist.core.list.service.ShoppingListValidationService;
import de.yannicklem.shoppinglist.core.user.security.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Service;

@Service
@RepositoryEventHandler(Item.class)
@RequiredArgsConstructor(onConstructor = @_(@Autowired ))
public class ItemService {

    private final CurrentUserService currentUserService;
    private final ItemValidationService itemValidationService;

    @HandleBeforeCreate(Item.class)
    public void handleBeforeCreate(Item item){
        
        if(item != null) {
            item.getOwners().add(currentUserService.getCurrentUser());
        }
        
        itemValidationService.validate(item);
    }
    
}
