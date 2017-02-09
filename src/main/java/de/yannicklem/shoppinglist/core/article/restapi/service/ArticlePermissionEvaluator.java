package de.yannicklem.shoppinglist.core.article.restapi.service;

import de.yannicklem.restutils.entity.owned.service.OwnedRestEntityPermissionEvaluator;
import de.yannicklem.restutils.entity.service.PermissionEvaluator;

import de.yannicklem.shoppinglist.core.article.entity.Article;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public class ArticlePermissionEvaluator implements PermissionEvaluator<Article> {

    private final OwnedRestEntityPermissionEvaluator ownedRestEntityPermissionEvaluator;

    @Override
    public boolean isAllowedToUpdate(Article oldObject, Article newObject, SLUser currentUser) {

        return ownedRestEntityPermissionEvaluator.isAllowedToUpdate(oldObject, newObject, currentUser);
    }


    @Override
    public boolean isAllowedToCreate(Article object, SLUser currentUser) {

        return ownedRestEntityPermissionEvaluator.isAllowedToCreate(object, currentUser);
    }


    @Override
    public boolean isAllowedToDelete(Article object, SLUser currentUser) {

        return ownedRestEntityPermissionEvaluator.isAllowedToDelete(object, currentUser);
    }


    @Override
    public boolean isAllowedToRead(Article object, SLUser currentUser) {

        return ownedRestEntityPermissionEvaluator.isAllowedToRead(object, currentUser);
    }
}
