package de.yannicklem.shoppinglist.core.list.persistence;

import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.list.entity.ShoppingList;
import de.yannicklem.shoppinglist.core.list.validation.ShoppingListValidationService;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.core.user.security.service.CurrentUserService;
import de.yannicklem.shoppinglist.exception.AlreadyExistsException;
import de.yannicklem.shoppinglist.exception.EntityInvalidException;
import de.yannicklem.shoppinglist.exception.NotFoundException;
import de.yannicklem.shoppinglist.restutils.service.EntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
@Service
public class ShoppingListService implements EntityService<ShoppingList, Long> {

    private final ShoppingListValidationService shoppingListValidationService;
    private final ShoppingListRepository shoppingListRepository;
    private final CurrentUserService currentUserService;

    private void handleBeforeCreate(ShoppingList shoppingList) {

        if (shoppingList == null) {
            throw new EntityInvalidException("shoppingList must not be null");
        }

        if (exists(shoppingList.getEntityId())) {
            throw new AlreadyExistsException("Shopping list already exists");
        }

        shoppingListValidationService.validate(shoppingList);

        Set<Item> items = shoppingList.getItems();

        for (Item item : items) {
            item.setOwners(shoppingList.getOwners());
            item.getArticle().setOwners(shoppingList.getOwners());
        }
    }


    private void handleBeforeUpdate(ShoppingList shoppingList) {

        if (shoppingList == null || !exists(shoppingList.getEntityId())) {
            throw new NotFoundException("Shopping list not found");
        }

        shoppingListValidationService.validate(shoppingList);

        Set<Item> items = shoppingList.getItems();

        for (Item item : items) {
            item.setOwners(shoppingList.getOwners());
            item.getArticle().setOwners(shoppingList.getOwners());
        }
    }


    public void handleBeforeDelete(ShoppingList shoppingList) {

        if (shoppingList == null) {
            throw new NotFoundException("shopping list not found.");
        }
    }


    @Override
    public List<ShoppingList> findAll(SLUser currentUser) {

        if (currentUser == null || currentUser.isAdmin()) {
            return shoppingListRepository.findAll();
        } else {
            return shoppingListRepository.findListsOwnedBy(currentUser);
        }
    }


    @Override
    public boolean exists(Long id) {

        if (id == null) {
            return false;
        }

        return shoppingListRepository.exists(id);
    }


    @Override
    public ShoppingList create(ShoppingList shoppingList) {

        handleBeforeCreate(shoppingList);

        return shoppingListRepository.save(shoppingList);
    }


    @Override
    public ShoppingList update(ShoppingList entity) {

        handleBeforeUpdate(entity);

        return shoppingListRepository.save(entity);
    }


    @Override
    public void delete(ShoppingList entity) {

        if (entity == null) {
            throw new NotFoundException("shopping list not found.");
        }

        SLUser currentUser = currentUserService.getCurrentUser();
        entity.getOwners().remove(currentUser);

        if (entity.getOwners().isEmpty()) {
            handleBeforeDelete(entity);

            shoppingListRepository.delete(entity);
        } else {
            update(entity);
        }
    }


    @Override
    public void deleteAll() {

        List<ShoppingList> all = findAll(currentUserService.getCurrentUser());

        for (ShoppingList shoppingList : all) {
            delete(shoppingList);
        }
    }


    @Override
    public ShoppingList findById(Long id) {

        if (id == null) {
            return null;
        }

        return shoppingListRepository.findOne(id);
    }


    public boolean exists(ShoppingList shoppingList) {

        if (shoppingList == null || shoppingList.getEntityId() == null) {
            return false;
        }

        return shoppingListRepository.exists(shoppingList.getEntityId());
    }


    public List<ShoppingList> findListsOwnedBy(SLUser slUser) {

        if (slUser == null) {
            return new ArrayList<>();
        }

        return shoppingListRepository.findListsOwnedBy(slUser);
    }


    public List<ShoppingList> findShoppingListsContainingItem(Item entity) {

        if (entity == null) {
            return new ArrayList<>();
        }

        return shoppingListRepository.findShoppingListsContainingItem(entity);
    }


    public Long countListsOf(SLUser currentUser) {

        Long count = shoppingListRepository.countListsOfUser(currentUser);

        return count == null ? 0 : count;
    }
}
