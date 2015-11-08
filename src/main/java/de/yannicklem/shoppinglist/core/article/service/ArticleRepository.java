package de.yannicklem.shoppinglist.core.article.service;

import de.yannicklem.shoppinglist.core.article.entity.Article;
import de.yannicklem.shoppinglist.core.article.entity.ArticleProjection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource(excerptProjection = ArticleProjection.class)
public interface ArticleRepository extends CrudRepository<Article, Long> {
}
