package de.yannicklem.shoppinglist.core.list.persistence;

import de.yannicklem.restutils.entity.service.AbstractEntityReadOnlyService;

import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.list.entity.ShoppingList;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


/**
 * @author  Yannic Klem - yann.klem@gmail.com
 */
@Transactional
@Service("readOnlyShoppingListService")
public class ShoppingListReadOnlyServiceImpl extends AbstractEntityReadOnlyService<ShoppingList, Long>
    implements ShoppingListReadOnlyService {

    private final ShoppingListRepository shoppingListRepository;

    @Autowired
    public ShoppingListReadOnlyServiceImpl(ShoppingListRepository shoppingListRepository) {

        super(shoppingListRepository);
        this.shoppingListRepository = shoppingListRepository;
    }

    @Override
    public List<ShoppingList> findListsOwnedBy(SLUser slUser) {

        if (slUser == null) {
            return new ArrayList<>();
        }

        return shoppingListRepository.findEntitiesOwnedBy(slUser);
    }


    @Override
    public List<ShoppingList> findShoppingListsContainingItem(Item entity) {

        if (entity == null) {
            return new ArrayList<>();
        }

        return shoppingListRepository.findShoppingListsContainingItem(entity);
    }


    @Override
    public Long countListsOf(SLUser currentUser) {

        Long count = shoppingListRepository.countListsOfUser(currentUser);

        return count == null ? 0 : count;
    }
}
