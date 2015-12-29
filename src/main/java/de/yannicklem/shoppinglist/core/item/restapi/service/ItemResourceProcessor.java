package de.yannicklem.shoppinglist.core.item.restapi.service;

import de.yannicklem.shoppinglist.core.article.entity.Article;
import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.persistence.ArticleService;
import de.yannicklem.shoppinglist.core.persistence.SLUserService;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.restutils.service.MyResourceProcessor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.hateoas.EntityLinks;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;


@Service
public class ItemResourceProcessor extends MyResourceProcessor<Item> {

    private final SLUserService slUserService;
    private final ArticleService articleService;

    @Autowired
    public ItemResourceProcessor(EntityLinks entityLinks, SLUserService slUserService, ArticleService articleService) {

        super(entityLinks);
        this.slUserService = slUserService;
        this.articleService = articleService;
    }

    @Override
    public Item initializeNestedEntities(Item entity) {

        Article article = entity.getArticle();

        if (article != null && article.getEntityId() != null) {
            entity.setArticle(articleService.findById(article.getEntityId()));
        }

        Set<SLUser> owners = entity.getOwners();
        Set<SLUser> persistedOwners = new HashSet<>();

        for (SLUser owner : owners) {
            if (owner.getEntityId() != null) {
                persistedOwners.add(slUserService.findById(owner.getEntityId()));
            }
        }

        entity.setOwners(persistedOwners);

        return entity;
    }
}
