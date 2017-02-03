package de.yannicklem.shoppinglist.core.list.persistence;

import de.yannicklem.restutils.entity.owned.service.AbstractOwnedEntityService;
import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.list.entity.ShoppingList;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.core.user.security.service.CurrentUserService;
import de.yannicklem.shoppinglist.core.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional
@Service
public class ShoppingListService extends AbstractOwnedEntityService<ShoppingList, Long> implements ShoppingListReadOnlyService {

    private final ShoppingListRepository shoppingListRepository;
    private final CurrentUserService currentUserService;
    private final ShoppingListPersistenceHandler shoppingListPersistenceHandler;
    private final ShoppingListReadOnlyService shoppingListReadOnlyService;

    @Autowired
    public ShoppingListService(ShoppingListRepository shoppingListRepository, CurrentUserService currentUserService,
                               ShoppingListPersistenceHandler shoppingListPersistenceHandler,
                               ShoppingListReadOnlyService shoppingListReadOnlyService) {

        super(shoppingListRepository, shoppingListPersistenceHandler, currentUserService);
        this.shoppingListRepository = shoppingListRepository;
        this.currentUserService = currentUserService;
        this.shoppingListPersistenceHandler = shoppingListPersistenceHandler;
        this.shoppingListReadOnlyService = shoppingListReadOnlyService;
    }


    @Override
    public void delete(ShoppingList entity) {

        if (entity == null) {
            throw new NotFoundException("shopping list not found.");
        }

        SLUser currentUser = currentUserService.getCurrentUser();
        entity.getOwners().remove(currentUser);

        if (entity.getOwners().isEmpty()) {
            shoppingListPersistenceHandler.handleBeforeDelete(entity);

            shoppingListRepository.delete(entity);
        } else {
            update(entity);
        }
    }

    @Override
    public List<ShoppingList> findListsOwnedBy(SLUser slUser) {

        return shoppingListReadOnlyService.findListsOwnedBy(slUser);
    }

    @Override
    public List<ShoppingList> findShoppingListsContainingItem(Item item) {

        return shoppingListReadOnlyService.findShoppingListsContainingItem(item);
    }

    @Override
    public Long countListsOf(SLUser currentUser) {

        return shoppingListReadOnlyService.countListsOf(currentUser);
    }
}
