package de.yannicklem.shoppinglist.core.item.restapi.service;

import de.yannicklem.restutils.service.MyResourceProcessor;

import de.yannicklem.shoppinglist.core.article.Article;
import de.yannicklem.shoppinglist.core.article.persistence.ArticleService;
import de.yannicklem.shoppinglist.core.exception.NotFoundException;
import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.item.persistence.ItemService;

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
    public Item initializeNestedEntities(Item entityDto) {

        if (entityDto != null) {
            Article article = entityDto.getArticle();

            if (article != null && article.getEntityId() != null) {
                Article existingArticle = articleService.findById(article.getEntityId()).orElseThrow(() ->
                            new NotFoundException("Article not found"));

                entityDto.setArticle(existingArticle);
            }
        }

        if (entityDto != null && itemService.exists(entityDto.getEntityId())) {
            Item existingItem = itemService.findById(entityDto.getEntityId()).orElseThrow(() ->
                        new NotFoundException("Item not found"));

            entityDto.setOwners(existingItem.getOwners());
            entityDto.setCreatedAt(existingItem.getCreatedAt());
        }

        return entityDto;
    }
}
