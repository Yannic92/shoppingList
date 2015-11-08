package de.yannicklem.shoppinglist.core.article.service;

import de.yannicklem.shoppinglist.core.article.entity.Article;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource
public interface ArticleRepository extends CrudRepository<Article, Long> {
}
