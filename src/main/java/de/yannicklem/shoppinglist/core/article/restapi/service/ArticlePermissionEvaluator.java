package de.yannicklem.shoppinglist.core.article.restapi.service;

import de.yannicklem.restutils.entity.owned.service.OwnedRestEntityPermissionEvaluator;
import de.yannicklem.restutils.entity.service.PermissionEvaluator;
import de.yannicklem.shoppinglist.core.article.dto.ArticleDto;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public class ArticlePermissionEvaluator implements PermissionEvaluator<ArticleDto> {

    private final OwnedRestEntityPermissionEvaluator ownedRestEntityPermissionEvaluator;

    @Override
    public boolean isAllowedToUpdate(ArticleDto oldObject, ArticleDto newObject, SLUser currentUser) {

        return ownedRestEntityPermissionEvaluator.isAllowedToUpdate(oldObject, newObject, currentUser);
    }


    @Override
    public boolean isAllowedToCreate(ArticleDto object, SLUser currentUser) {

        return ownedRestEntityPermissionEvaluator.isAllowedToCreate(object, currentUser);
    }


    @Override
    public boolean isAllowedToDelete(ArticleDto object, SLUser currentUser) {

        return ownedRestEntityPermissionEvaluator.isAllowedToDelete(object, currentUser);
    }


    @Override
    public boolean isAllowedToRead(ArticleDto object, SLUser currentUser) {

        return ownedRestEntityPermissionEvaluator.isAllowedToRead(object, currentUser);
    }
}
