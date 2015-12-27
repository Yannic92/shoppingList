package de.yannicklem.shoppinglist.core.article.service;

import de.yannicklem.shoppinglist.core.article.entity.Article;

import org.springframework.data.repository.CrudRepository;


public interface ArticleRepository extends CrudRepository<Article, Long> {
}
