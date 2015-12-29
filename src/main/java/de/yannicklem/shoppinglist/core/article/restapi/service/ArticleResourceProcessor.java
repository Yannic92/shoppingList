package de.yannicklem.shoppinglist.core.article.restapi.service;

import de.yannicklem.shoppinglist.core.article.entity.Article;
import de.yannicklem.shoppinglist.core.persistence.ArticleService;
import de.yannicklem.shoppinglist.core.persistence.SLUserService;
import de.yannicklem.shoppinglist.restutils.service.MyResourceProcessor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.hateoas.EntityLinks;

import org.springframework.stereotype.Service;


@Service
public class ArticleResourceProcessor extends MyResourceProcessor<Article> {

    private final SLUserService slUserService;
    private final ArticleService articleService;

    @Autowired
    public ArticleResourceProcessor(EntityLinks entityLinks, SLUserService slUserService,
        ArticleService articleService) {

        super(entityLinks);
        this.slUserService = slUserService;
        this.articleService = articleService;
    }

    @Override
    public Article initializeNestedEntities(Article entity) {

        if (entity != null && articleService.exists(entity.getEntityId())) {
            entity.setOwners(articleService.findById(entity.getEntityId()).getOwners());
        }

        return entity;
    }
}
