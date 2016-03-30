package de.yannicklem.shoppinglist.core.article.persistence;

import de.yannicklem.shoppinglist.core.article.entity.Article;
import de.yannicklem.shoppinglist.core.article.validation.ArticleValidationService;
import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.item.persistence.ItemService;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.core.user.security.service.CurrentUserService;
import de.yannicklem.shoppinglist.exception.NotFoundException;
import de.yannicklem.shoppinglist.restutils.service.EntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public class ArticleService implements EntityService<Article, Long> {

    private final ArticleValidationService articleValidationService;
    private final ArticleRepository articleRepository;
    private final ItemService itemService;
    private final CurrentUserService currentUserService;

    public void handleBeforeCreate(Article article) {

        articleValidationService.validate(article);
    }


    public void handleBeforeUpdate(Article article) {

        articleValidationService.validate(article);
    }


    public void handleAfterCreate(Article article) {

    }


    public void handleAfterDelete(Article article) {

    }


    public void handleAfterUpdate(Article article) {

    }


    @Override
    public Article findById(Long id) {

        if (id == null) {
            return null;
        }

        return articleRepository.findOne(id);
    }


    @Override
    public List<Article> findAll(SLUser currentUser) {

        if (currentUser == null || currentUser.isAdmin()) {
            return articleRepository.findAll();
        } else {
            return articleRepository.findArticlesOwnedBy(currentUser);
        }
    }


    @Override
    public boolean exists(Long id) {

        if (id == null) {
            return false;
        }

        return articleRepository.exists(id);
    }


    @Override
    public Article create(Article article) {

        handleBeforeCreate(article);

        Article createdArticle = articleRepository.save(article);

        handleAfterCreate(article);

        return createdArticle;
    }


    @Override
    public Article update(Article article) {

        handleBeforeUpdate(article);

        Article updatedArticle = articleRepository.save(article);

        handleAfterUpdate(article);

        return updatedArticle;
    }


    @Override
    public void delete(Article article) {

        if (article == null) {
            throw new NotFoundException("Article not found");
        }

        SLUser currentUser = currentUserService.getCurrentUser();
        article.getOwners().remove(currentUser);

        if (article.getOwners().isEmpty()) {
            handleBeforeDelete(article);

            articleRepository.delete(article);

            handleAfterDelete(article);
        } else {
            update(article);
        }
    }


    private void handleBeforeDelete(Article article) {

        List<Item> itemsByArticle = itemService.findItemsByArticle(article);

        for (Item item : itemsByArticle) {
            itemService.delete(item);
        }
    }


    @Override
    public void deleteAll() {

        List<Article> all = findAll(currentUserService.getCurrentUser());

        for (Article article : all) {
            delete(article);
        }
    }


    public List<Article> findArticlesOwnedBy(SLUser slUser) {

        if (slUser == null) {
            return new ArrayList<>();
        }

        return articleRepository.findArticlesOwnedBy(slUser);
    }


    public Article findByName(String name) {

        if (name == null) {
            return null;
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
