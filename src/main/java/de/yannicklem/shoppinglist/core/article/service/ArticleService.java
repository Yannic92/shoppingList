package de.yannicklem.shoppinglist.core.article.service;

import de.yannicklem.shoppinglist.core.article.entity.Article;
import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.item.service.ItemValidationService;
import de.yannicklem.shoppinglist.core.user.security.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
@RepositoryEventHandler(Article.class)
public class ArticleService {

    private final CurrentUserService currentUserService;
    private final ArticleValidationService articleValidationService;
    
    @HandleBeforeCreate(Article.class)
    public void handleBeforeCreate(Article article){
        
        if(article != null) {
            article.getOwners().add(currentUserService.getCurrentUser());
        }
        
        articleValidationService.validate(article);
    }
    
}
