package de.yannicklem.shoppinglist.core.item.restapi.service;

import de.yannicklem.shoppinglist.core.OwnedRestEntityPermissionEvaluator;
import de.yannicklem.shoppinglist.core.article.entity.Article;
import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.restutils.service.PermissionEvaluator;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public class ItemPermissionEvaluator implements PermissionEvaluator<Item> {

    private final OwnedRestEntityPermissionEvaluator ownedRestEntityPermissionEvaluator;

    @Override
    public boolean isAllowedToUpdate(Item oldObject, Item newObject, SLUser currentUser) {

        return ownedRestEntityPermissionEvaluator.isAllowedToUpdate(oldObject, newObject, currentUser);
    }


    @Override
    public boolean isAllowedToCreate(Item object, SLUser currentUser) {

        if (!ownedRestEntityPermissionEvaluator.isAllowedToCreate(object, currentUser)) {
            return false;
        }

        Article updatedArticle = new Article(object.getArticle());
        updatedArticle.setOwners(object.getOwners());

        return ownedRestEntityPermissionEvaluator.isAllowedToUpdate(object.getArticle(), updatedArticle, currentUser);
    }


    @Override
    public boolean isAllowedToDelete(Item object, SLUser currentUser) {

        return ownedRestEntityPermissionEvaluator.isAllowedToDelete(object, currentUser);
    }


    @Override
    public boolean isAllowedToRead(Item object, SLUser currentUser) {

        return ownedRestEntityPermissionEvaluator.isAllowedToRead(object, currentUser);
    }
}
