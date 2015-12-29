package de.yannicklem.shoppinglist.core.article.service;

import de.yannicklem.shoppinglist.core.article.entity.Article;
import de.yannicklem.shoppinglist.core.user.security.service.CurrentUserService;
import de.yannicklem.shoppinglist.restutils.service.EntityService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public class ArticleService implements EntityService<Article, Long> {

    private final CurrentUserService currentUserService;
    private final ArticleValidationService articleValidationService;
    private final ArticleRepository articleRepository;

    public void handleBeforeCreate(Article article) {

        if (article != null) {
            article.getOwners().add(currentUserService.getCurrentUser());
        }

        articleValidationService.validate(article);
    }


    @Override
    public Article findById(Long id) {

        return articleRepository.findOne(id);
    }


    @Override
    public List<Article> findAll() {

        return articleRepository.findAll();
    }


    @Override
    public boolean exists(Long id) {

        return articleRepository.exists(id);
    }


    @Override
    public Article create(Article article) {

        handleBeforeCreate(article);

        return articleRepository.save(article);
    }


    @Override
    public Article update(Article article) {

        return articleRepository.save(article);
    }


    @Override
    public void delete(Article article) {

        articleRepository.delete(article);
    }
}
