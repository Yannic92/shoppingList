package de.yannicklem.shoppinglist.core.article.persistence;

import de.yannicklem.restutils.entity.service.EntityPersistenceHandler;

import de.yannicklem.shoppinglist.core.article.Article;
import de.yannicklem.shoppinglist.core.article.validation.ArticleValidationService;
import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.item.persistence.ItemService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author  Yannic Klem - yann.klem@gmail.com
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public class ArticlePersistenceHandler implements EntityPersistenceHandler<Article> {

    private final ArticleValidationService articleValidationService;
    private final ItemService itemService;

    @Override
    public void handleBeforeCreate(Article article) {

        articleValidationService.validate(article);
    }


    @Override
    public void handleBeforeUpdate(Article article) {

        articleValidationService.validate(article);
    }


    @Override
    public void handleBeforeDelete(Article article) {

        List<Item> itemsByArticle = itemService.findItemsByArticle(article);

        for (Item item : itemsByArticle) {
            itemService.delete(item);
        }
    }
}
