package de.yannicklem.shoppinglist.core.article.persistence;

import de.yannicklem.shoppinglist.core.article.entity.Article;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;

import de.yannicklem.restutils.entity.owned.service.OwnedRestEntityRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface ArticleRepository extends OwnedRestEntityRepository<Article, Long> {


    Optional<Article> findByName(String name);


    @Query("SELECT COUNT(article) FROM Article article INNER JOIN article.owners owner WHERE :user = owner")
    Long countArticlesOfUser(@Param("user") SLUser user);
}
