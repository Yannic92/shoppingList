package de.yannicklem.shoppinglist.core.article.service;

import de.yannicklem.shoppinglist.core.article.entity.Article;
import de.yannicklem.shoppinglist.exception.EntityInvalidException;

import org.springframework.stereotype.Service;


@Service
public class ArticleValidationService {

    public void validate(Article article) throws EntityInvalidException {
    }
}
