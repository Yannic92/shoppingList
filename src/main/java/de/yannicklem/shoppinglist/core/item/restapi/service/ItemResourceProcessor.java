package de.yannicklem.shoppinglist.core.item.restapi.service;

import de.yannicklem.shoppinglist.core.article.entity.Article;
import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.persistence.ArticleService;
import de.yannicklem.shoppinglist.core.persistence.ItemService;
import de.yannicklem.shoppinglist.restutils.service.MyResourceProcessor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.hateoas.EntityLinks;

import org.springframework.stereotype.Service;


@Service
public class ItemResourceProcessor extends MyResourceProcessor<Item> {

    private final ArticleService articleService;
    private final ItemService itemService;

    @Autowired
    public ItemResourceProcessor(EntityLinks entityLinks, ArticleService articleService, ItemService itemService) {

        super(entityLinks);
        this.itemService = itemService;
        this.articleService = articleService;
    }

    @Override
    public Item initializeNestedEntities(Item entity) {

        if (entity != null) {
            Article article = entity.getArticle();

            if (article != null && article.getEntityId() != null) {
                entity.setArticle(articleService.findById(article.getEntityId()));
            }
        }

        if (entity != null && itemService.exists(entity.getEntityId())) {
            entity.setOwners(itemService.findById(entity.getEntityId()).getOwners());
        }

        return entity;
    }
}
