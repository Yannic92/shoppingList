package de.yannicklem.shoppinglist.core.persistence;

import de.yannicklem.shoppinglist.core.list.entity.ShoppingList;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ShoppingListRepository extends CrudRepository<ShoppingList, Long> {

    @Override
    List<ShoppingList> findAll();


    @Query("SELECT s FROM ShoppingList s WHERE :user MEMBER OF s.owners")
    List<ShoppingList> findListsOwnedBy(@Param("user") SLUser slUser);
}
