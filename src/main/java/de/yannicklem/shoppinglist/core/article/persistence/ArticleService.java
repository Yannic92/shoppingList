package de.yannicklem.shoppinglist.core.article.persistence;

import de.yannicklem.restutils.entity.owned.service.OwnedEntityService;
import de.yannicklem.shoppinglist.core.article.entity.Article;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;

import java.util.List;
import java.util.Optional;

/**
 * @author Yannic Klem - yann.klem@gmail.com
 */
public interface ArticleService extends OwnedEntityService<Article, Long> {
    void delete(Article article);

    List<Article> findArticlesOwnedBy(SLUser slUser);

    Optional<Article> findByName(String name);

    boolean isUsedInItem(Article article);

    Long countArticlesOfOwner(SLUser user);
}
