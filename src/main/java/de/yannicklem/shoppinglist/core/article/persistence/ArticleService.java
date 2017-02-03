package de.yannicklem.shoppinglist.core.article.persistence;

import de.yannicklem.restutils.entity.owned.service.AbstractOwnedEntityService;
import de.yannicklem.shoppinglist.core.article.entity.Article;
import de.yannicklem.shoppinglist.core.item.persistence.ItemService;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.core.user.security.service.CurrentUserService;
import de.yannicklem.shoppinglist.core.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class ArticleService extends AbstractOwnedEntityService<Article, Long> {

    private final ArticleRepository articleRepository;
    private final ArticlePersistenceHandler articlePersistenceHandler;
    private final ItemService itemService;
    private final CurrentUserService currentUserService;

    @Autowired
    public ArticleService(ArticleRepository articleRepository, ArticlePersistenceHandler articlePersistenceHandler,
                          ItemService itemService, CurrentUserService currentUserService) {

        super(articleRepository, articlePersistenceHandler, currentUserService);
        this.articleRepository = articleRepository;
        this.articlePersistenceHandler = articlePersistenceHandler;
        this.itemService = itemService;
        this.currentUserService = currentUserService;

    }

    @Override
    public void delete(Article article) {

        if (article == null) {
            throw new NotFoundException("Article not found");
        }

        SLUser currentUser = this.currentUserService.getCurrentUser();
        article.getOwners().remove(currentUser);

        if (article.getOwners().isEmpty()) {
            articlePersistenceHandler.handleBeforeDelete(article);

            articleRepository.delete(article);

            articlePersistenceHandler.handleAfterDelete(article);
        } else {
            update(article);
        }
    }


    public List<Article> findArticlesOwnedBy(SLUser slUser) {

        if (slUser == null) {
            return new ArrayList<>();
        }

        return articleRepository.findEntitiesOwnedBy(slUser);
    }


    public Optional<Article> findByName(String name) {

        if (name == null) {
            return Optional.empty();
        }

        return articleRepository.findByName(name);
    }


    public boolean isUsedInItem(Article article) {

        return !itemService.findItemsByArticle(article).isEmpty();
    }


    public Long countArticlesOfOwner(SLUser user) {

        Long count = articleRepository.countArticlesOfUser(user);

        return count == null ? 0 : count;
    }
}
