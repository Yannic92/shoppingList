package de.yannicklem.shoppinglist.core.article.restapi.service;

import de.yannicklem.shoppinglist.core.article.entity.Article;
import de.yannicklem.shoppinglist.core.persistence.ArticleService;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.exception.BadRequestException;
import de.yannicklem.shoppinglist.exception.PermissionDeniedException;
import de.yannicklem.shoppinglist.restutils.service.RequestHandler;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public class ArticleRequestHandler implements RequestHandler<Article> {

    private final ArticlePermissionEvaluator articlePermissionEvaluator;
    private final ArticleService articleService;

    @Override
    public void handleBeforeCreate(Article entity, SLUser currentUser) {

        if (entity != null) {
            Article articleWithSameName = articleService.findByName(entity.getName());

            if (articleWithSameName != null) {
                entity.setEntityId(articleWithSameName.getEntityId());
                entity.setOwners(articleWithSameName.getOwners());
                entity.setPriceInEuro(articleWithSameName.getPriceInEuro());
            }

            if (currentUser != null) {
                entity.getOwners().add(currentUser);
            }
        }

        if (!articlePermissionEvaluator.isAllowedToCreate(entity, currentUser)) {
            throw new PermissionDeniedException();
        }
    }


    @Override
    public void handleBeforeUpdate(Article oldEntity, Article newEntity, SLUser currentUser) {

        if (!articlePermissionEvaluator.isAllowedToUpdate(oldEntity, newEntity, currentUser)) {
            throw new PermissionDeniedException();
        }
    }


    @Override
    public void handleRead(Article entity, SLUser currentUser) {

        if (!articlePermissionEvaluator.isAllowedToRead(entity, currentUser)) {
            throw new PermissionDeniedException();
        }
    }


    @Override
    public void handleBeforeDelete(Article entity, SLUser currentUser) {

        if (!articlePermissionEvaluator.isAllowedToDelete(entity, currentUser)) {
            throw new PermissionDeniedException();
        }

        if (articleService.isUsedInItem(entity)) {
            throw new BadRequestException("Lösche zuerst den Artikel aus deiner Einkaufsliste");
        }
    }


    @Override
    public void handleAfterCreate(Article entity, SLUser currentUser) {
    }


    @Override
    public void handleAfterUpdate(Article oldEntity, Article newEntity, SLUser currentUser) {
    }


    @Override
    public void handleAfterDelete(Article entity, SLUser currentUser) {
    }
}
