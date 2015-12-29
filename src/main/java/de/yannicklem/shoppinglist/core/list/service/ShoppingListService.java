package de.yannicklem.shoppinglist.core.list.service;

import de.yannicklem.shoppinglist.core.list.entity.ShoppingList;
import de.yannicklem.shoppinglist.core.user.security.service.CurrentUserService;
import de.yannicklem.shoppinglist.exception.AlreadyExistsException;
import de.yannicklem.shoppinglist.exception.NotFoundException;
import de.yannicklem.shoppinglist.restutils.service.EntityService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public class ShoppingListService implements EntityService<ShoppingList, Long> {

    private final CurrentUserService currentUserService;
    private final ShoppingListValidationService shoppingListValidationService;
    private final ShoppingListRepository shoppingListRepository;

    private void handleBeforeCreate(ShoppingList shoppingList) {

        if (shoppingList != null) {
            shoppingList.getOwners().add(currentUserService.getCurrentUser());
        }

        if (shoppingList != null && exists(shoppingList.getEntityId())) {
            throw new AlreadyExistsException("Shopping list already exists");
        }

        shoppingListValidationService.validate(shoppingList);
    }


    private void handleBeforeUpdate(ShoppingList shoppingList) {

        if (shoppingList == null || !exists(shoppingList.getEntityId())) {
            throw new NotFoundException("Shopping list not found");
        }

        shoppingListValidationService.validate(shoppingList);
    }


    public void handleBeforeDelete(ShoppingList shoppingList) {

        if (shoppingList == null) {
            throw new NotFoundException("shopping list not found.");
        }
    }


    @Override
    public List<ShoppingList> findAll() {

        return shoppingListRepository.findAll();
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

        handleBeforeDelete(entity);

        shoppingListRepository.delete(entity);
    }


    @Override
    public ShoppingList findById(Long id) {

        return shoppingListRepository.findOne(id);
    }


    public boolean exists(ShoppingList shoppingList) {

        if (shoppingList == null || shoppingList.getEntityId() == null) {
            return false;
        }

        return shoppingListRepository.exists(shoppingList.getEntityId());
    }
}
