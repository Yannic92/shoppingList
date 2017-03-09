package de.yannicklem.shoppinglist.core.article.restapi.service;

import de.yannicklem.restutils.service.MyResourceProcessor;

import de.yannicklem.shoppinglist.core.article.Article;
import de.yannicklem.shoppinglist.core.article.dto.ArticleDto;
import de.yannicklem.shoppinglist.core.article.persistence.ArticleService;
import de.yannicklem.shoppinglist.core.exception.NotFoundException;
import de.yannicklem.shoppinglist.core.user.persistence.SLUserService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.hateoas.EntityLinks;

import org.springframework.stereotype.Service;


@Service
public class ArticleResourceProcessor extends MyResourceProcessor<ArticleDto> {

    private final SLUserService slUserService;
    private final ArticleService articleService;

    @Autowired
    public ArticleResourceProcessor(EntityLinks entityLinks, SLUserService slUserService,
        ArticleService articleService) {

        super(entityLinks);
        this.slUserService = slUserService;
        this.articleService = articleService;
    }

    @Override
    public ArticleDto initializeNestedEntities(ArticleDto articleDto) {

        if (articleDto != null && articleService.exists(articleDto.getEntityId())) {
            Article existingArticle = articleService.findById(articleDto.getEntityId()).orElseThrow(() ->
                        new NotFoundException("Article not found"));

            articleDto.setOwners(existingArticle.getOwners());
        }

        return articleDto;
    }
}
