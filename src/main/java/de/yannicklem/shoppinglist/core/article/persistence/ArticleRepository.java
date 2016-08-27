package de.yannicklem.shoppinglist.core.article.persistence;

import de.yannicklem.shoppinglist.core.article.entity.Article;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ArticleRepository extends CrudRepository<Article, Long> {

    @Override
    List<Article> findAll();


    @Query("SELECT article FROM Article article INNER JOIN article.owners owner WHERE :user = owner")
    List<Article> findArticlesOwnedBy(@Param("user") SLUser slUser);


    Article findByName(String name);


    @Query("SELECT COUNT(article) FROM Article article INNER JOIN article.owners owner WHERE :user = owner")
    Long countArticlesOfUser(@Param("user") SLUser user);
}
