package de.yannicklem.shoppinglist.core.article.restapi.service;

import de.yannicklem.shoppinglist.core.article.entity.Article;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.restutils.service.RequestHandler;

import org.springframework.stereotype.Service;


@Service
public class ArticleRequestHandler implements RequestHandler<Article> {

    @Override
    public void handleBeforeCreate(Article entity, SLUser currentUser) {
    }


    @Override
    public void handleBeforeUpdate(Article oldEntity, Article newEntity, SLUser currentUser) {
    }


    @Override
    public void handleRead(Article entity, SLUser currentUser) {
    }


    @Override
    public void handleBeforeDelete(Article entity, SLUser currentUser) {
    }


    @Override
    public void handleAfterCreate(Article entity, SLUser currentUser) {
    }


    @Override
    public void handleAfterUpdate(Article oldEntity, Article newEntity, SLUser currentUser) {
    }


    @Override
    public void handleAfterDelete(Article entity, SLUser currentUser) {
    }
}
