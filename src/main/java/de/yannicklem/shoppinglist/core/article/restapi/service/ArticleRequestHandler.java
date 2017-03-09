package de.yannicklem.shoppinglist.core.article.restapi.service;

import de.yannicklem.restutils.service.RequestHandler;

import de.yannicklem.shoppinglist.core.article.Article;
import de.yannicklem.shoppinglist.core.article.dto.ArticleDto;
import de.yannicklem.shoppinglist.core.article.dto.ArticleMapper;
import de.yannicklem.shoppinglist.core.article.persistence.ArticleService;
import de.yannicklem.shoppinglist.core.exception.BadRequestException;
import de.yannicklem.shoppinglist.core.exception.PermissionDeniedException;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public class ArticleRequestHandler implements RequestHandler<ArticleDto> {

    private static final int MAX_ARTICLES_PER_USER = 10000;

    private final ArticlePermissionEvaluator articlePermissionEvaluator;
    private final ArticleMapper articleMapper;
    private final ArticleService articleService;

    @Override
    public void handleBeforeCreate(ArticleDto entityDto, SLUser currentUser) {

        if (entityDto != null) {
            Optional<Article> articleWithSameNameOptional = articleService.findByName(entityDto.getName());

            if (articleWithSameNameOptional.isPresent()) {
                Article articleWithSameName = articleWithSameNameOptional.get();
                entityDto.setEntityId(articleWithSameName.getEntityId());
                entityDto.setOwners(articleWithSameName.getOwners());
                entityDto.setPriceInEuro(articleWithSameName.getPriceInEuro());
            }

            if (currentUser != null) {
                entityDto.getOwners().add(currentUser);
            }
        }

        if (!articlePermissionEvaluator.isAllowedToCreate(entityDto, currentUser)) {
            throw new PermissionDeniedException();
        }

        assert entityDto != null;

        for (SLUser owner : entityDto.getOwners()) {
            Long numberOfItems = articleService.countArticlesOfOwner(owner);

            if (numberOfItems > MAX_ARTICLES_PER_USER) {
                throw new BadRequestException(String.format(
                        "Der Nutzer %s hat das Maximum von %d Wörterbucheinträgen erreicht", owner.getUsername(),
                        MAX_ARTICLES_PER_USER));
            }
        }
    }


    @Override
    public void handleBeforeUpdate(ArticleDto oldEntityDto, ArticleDto newEntityDto, SLUser currentUser) {

        if (!articlePermissionEvaluator.isAllowedToUpdate(oldEntityDto, newEntityDto, currentUser)) {
            throw new PermissionDeniedException();
        }

        assert newEntityDto != null;

        for (SLUser owner : newEntityDto.getOwners()) {
            Long numberOfItems = articleService.countArticlesOfOwner(owner);

            if (numberOfItems > MAX_ARTICLES_PER_USER) {
                throw new BadRequestException(String.format(
                        "Der Nutzer %s hat das Maximum von %d Wörterbucheinträgen erreicht", owner.getUsername(),
                        MAX_ARTICLES_PER_USER));
            }
        }
    }


    @Override
    public void handleRead(ArticleDto entityDto, SLUser currentUser) {

        if (!articlePermissionEvaluator.isAllowedToRead(entityDto, currentUser)) {
            throw new PermissionDeniedException();
        }
    }


    @Override
    public void handleBeforeDelete(ArticleDto entityDto, SLUser currentUser) {

        if (!articlePermissionEvaluator.isAllowedToDelete(entityDto, currentUser)) {
            throw new PermissionDeniedException();
        }

        if (articleService.isUsedInItem(articleMapper.toEntity(entityDto))) {
            throw new BadRequestException("Lösche zuerst den Artikel aus deiner Einkaufsliste");
        }
    }


    @Override
    public void handleAfterCreate(ArticleDto entityDto, SLUser currentUser) {
    }


    @Override
    public void handleAfterUpdate(ArticleDto oldEntityDto, ArticleDto newEntityDto, SLUser currentUser) {
    }


    @Override
    public void handleAfterDelete(ArticleDto entityDto, SLUser currentUser) {
    }
}
