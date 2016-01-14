package de.yannicklem.shoppinglist.core.persistence;

import de.yannicklem.shoppinglist.core.article.entity.Article;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


@CacheConfig(cacheNames = "articles")
public interface ArticleRepository extends CrudRepository<Article, Long> {

    @Override
    @Cacheable
    List<Article> findAll();


    @Cacheable
    @Query("SELECT a FROM Article a WHERE :user MEMBER OF a.owners")
    List<Article> findArticlesOwnedBy(@Param("user") SLUser slUser);


    @Cacheable
    Article findByName(String name);


    @Query("SELECT COUNT(a) FROM Article a WHERE :user MEMBER OF a.owners")
    Long countArticlesOfUser(@Param("user") SLUser user);


    @Cacheable
    @Override
    Article findOne(Long aLong);


    @Cacheable
    @Override
    boolean exists(Long aLong);
}
