package de.yannicklem.shoppinglist.core.article.restapi.service;

import de.yannicklem.shoppinglist.core.article.entity.Article;
import de.yannicklem.shoppinglist.core.persistence.SLUserService;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.restutils.service.MyResourceProcessor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.hateoas.EntityLinks;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;


@Service
public class ArticleResourceProcessor extends MyResourceProcessor<Article> {

    private final SLUserService slUserService;

    @Autowired
    public ArticleResourceProcessor(EntityLinks entityLinks, SLUserService slUserService) {

        super(entityLinks);
        this.slUserService = slUserService;
    }

    @Override
    public Article initializeNestedEntities(Article entity) {

        Set<SLUser> owners = entity.getOwners();
        Set<SLUser> persistedOwners = new HashSet<>();

        for (SLUser owner : owners) {
            if (owner.getEntityId() != null) {
                persistedOwners.add(slUserService.findById(owner.getEntityId()));
            }
        }

        entity.setOwners(persistedOwners);

        return entity;
    }
}
