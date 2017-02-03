package de.yannicklem.shoppinglist.core.item.restapi.service;

import de.yannicklem.shoppinglist.core.article.entity.Article;
import de.yannicklem.shoppinglist.core.article.persistence.ArticleService;
import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.item.persistence.ItemService;
import de.yannicklem.shoppinglist.core.exception.NotFoundException;
import de.yannicklem.restutils.service.MyResourceProcessor;

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

                Article existingArticle = articleService.findById(article.getEntityId()).orElseThrow(
                        () -> new NotFoundException("Article not found")
                );

                entity.setArticle(existingArticle );
            }
        }

        if (entity != null && itemService.exists(entity.getEntityId())) {

            Item existingItem = itemService.findById(entity.getEntityId()).orElseThrow(
                    () -> new NotFoundException("Item not found")
            );

            entity.setOwners(existingItem.getOwners());
            entity.setCreatedAt(existingItem.getCreatedAt());
        }

        return entity;
    }
}
